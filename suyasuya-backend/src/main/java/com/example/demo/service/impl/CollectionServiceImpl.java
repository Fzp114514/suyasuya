package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.PageResult;
import com.example.demo.entity.Collection;
import com.example.demo.entity.VideoCollection;
import com.example.demo.mapper.CollectionMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.VideoCollectionMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.CollectionService;
import com.example.demo.vo.CollectedVideoVO;
import com.example.demo.vo.CollectionVideoVO;
import com.example.demo.vo.UserCollectionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements CollectionService {
    // 定义收藏夹名称最大长度（与数据库字段一致）
    private static final int MAX_COLLECTION_NAME_LENGTH = 50;
    // 定义一次请求能获取的最多收藏记录
    private static final int MAX_LIMIT = 100;
    // 定义单次分页查询最大页码
    private static final long MAX_PAGE_SIZE = 100L;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private VideoCollectionMapper videoCollectionMapper;

    @Transactional
    @Override
    public Collection createCollection(Integer userId, String collectionName, String description) {
        // 参数基础校验
        validateParams(userId, collectionName);
        Collection existingCollection = baseMapper.selectByUserIdAndName(userId, collectionName);
        if (existingCollection != null) {
            // 如果存在同名收藏夹，抛出异常或返回错误信息
            throw new IllegalArgumentException("已经存在同名的收藏夹！");
        }

        // 处理收藏夹名称
        String processedName = processCollectionName(collectionName);

        // 构建收藏夹实体
        Collection collection = buildCollection(userId, processedName, description);

        System.out.println((collection));
        // 持久化到数据库
        baseMapper.insert(collection);

        return collection;
    }

    @Override
    public List<Collection> getUserCollections(Integer userId) {
        // 参数校验
        validateUserId(userId);

        // 构建查询条件
        QueryWrapper<Collection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByAsc("create_time"); // 按创建时间升序

        // 执行查询
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addVideoToCollection(Integer userId, Integer collectionId, Integer videoId) {
        // 1. 参数基础校验
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        if (collectionId == null || collectionId <= 0) {
            throw new RuntimeException("无效收藏夹ID");
        }
        if (videoId == null || videoId <= 0) {
            throw new RuntimeException("无效视频ID");
        }

        // 2. 验证收藏夹存在性
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            throw new RuntimeException("收藏夹不存在");
        }
        if (!userId.equals(collection.getUserId())) {
            throw new RuntimeException("无权操作他人收藏夹");
        }

        // 3. 验证视频存在性及归属权
        if (videoMapper.selectById(videoId) == null) {
            throw new RuntimeException("视频不存在");
        }

        // 4. 检查是否已收藏（优化为一次查询）
        boolean exists = videoCollectionMapper.exists(
                new QueryWrapper<VideoCollection>()
                        .eq("collection_id", collectionId)
                        .eq("video_id", videoId)
        );
        if (exists) {
            throw new RuntimeException("视频已存在于收藏夹");
        }

        // 5. 创建关联记录
        VideoCollection relation = new VideoCollection();
        relation.setCollectionId(collectionId);
        relation.setVideoId(videoId);
        relation.setCollectTime(new Date()); // 设置收藏时间

        try {
            videoCollectionMapper.insert(relation);
        } catch (DuplicateKeyException e) {
            log.warn("重复收藏，collectionId: {}, videoId: {}");
            throw new RuntimeException("请勿重复收藏");
        }

        // 6. 批量更新计数器（事务保障）
        updateCounters(collectionId, videoId);
    }

    private void updateCounters(Integer collectionId, Integer videoId) {
        // 更新收藏夹计数
        int collectionUpdated = collectionMapper.updateVideoCount(collectionId, 1);
        // 更新视频收藏计数
        int videoUpdated = videoMapper.updateCollectionCount(videoId, 1);

        if (collectionUpdated == 0 || videoUpdated == 0) {
            log.error("计数更新失败 collectionId: {}, videoId: {}");
            throw new RuntimeException("系统计数异常");
        }
    }

    @Override
    public List<UserCollectionVO> getVideoCollectionsByUser(Integer userId, Integer videoId) {
        // 参数校验
        validateUserAndVideo(userId, videoId);

        // 验证用户和视频存在性
        validateUserExists(userId);
        validateVideoExists(videoId);

        // 执行联合查询
        return videoCollectionMapper.selectCollectionsByUserAndVideo(userId, videoId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeVideoFromCollection(Integer userId, Integer collectionId, Integer videoId) {
        // 参数校验
        validateIds(userId, collectionId, videoId);

        // 验证收藏夹存在性
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            throw new RuntimeException("收藏夹不存在");
        }
        if (!userId.equals(collection.getUserId())) {
            throw new RuntimeException("无权操作他人收藏夹");
        }

        // 验证视频存在性
        if (videoMapper.selectById(videoId) == null) {
            throw new RuntimeException("视频不存在");
        }

        // 删除关联记录
        int deleted = videoCollectionMapper.delete(
                new QueryWrapper<VideoCollection>()
                        .eq("collection_id", collectionId)
                        .eq("video_id", videoId)
        );

        if (deleted == 0) {
            throw new RuntimeException("视频不在该收藏夹中");
        }

        // 5. 批量更新计数器（事务保障）
        updateCounters(collectionId, videoId, -1);
    }

    private void updateCounters(Integer collectionId, Integer videoId, int delta) {
        // 更新收藏夹计数
        int collectionUpdated = collectionMapper.updateVideoCount(collectionId, delta);
        // 更新视频收藏计数
        int videoUpdated = videoMapper.updateCollectionCount(videoId, delta);

        if (collectionUpdated == 0 || videoUpdated == 0) {
            log.error("计数更新失败 collectionId: {}, videoId: {}, delta: {}");
            throw new RuntimeException("系统计数异常");
        }
    }

    @Override
    public List<CollectedVideoVO> getRecentCollectedVideos(Integer userId, Integer n) {
        // 参数校验
        validateParams(userId, n);

        // 验证用户存在性
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取收藏记录
        return videoCollectionMapper.selectRecentCollectedVideos(userId,
                Math.min(n, MAX_LIMIT));
    }

    @Override
    public PageResult<CollectionVideoVO> getCollectionVideosPage(Integer collectionId, Long pageNum, Long pageSize) {
        validateCollection(collectionId);
        validatePageParams(pageNum, pageSize);

        // 计算偏移量
        Long offset = (pageNum - 1) * pageSize;

        // 分页查询
        List<CollectionVideoVO> list = videoCollectionMapper.selectVideosPage(
                collectionId, offset, Math.min(pageSize, MAX_PAGE_SIZE));

        // 总数查询
        Long total = videoCollectionMapper.selectVideoCount(collectionId);

        // 计算总页数
        Long pages = (total + pageSize - 1) / pageSize; // 向上取整

        return new PageResult<>(list, total, pageNum, pageSize, pages);
    }
    @Override
    @Transactional
    public void updateCollection(Integer userId, Integer collectionId, String collectionName, String description) {
        // 参数校验
        if (userId == null || collectionId == null) {
            throw new IllegalArgumentException("用户ID和收藏夹ID不能为空");
        }

        // 查询收藏夹信息
        Collection existing = collectionMapper.selectById(collectionId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("收藏夹不存在或权限不足");
        }

        // 校验收藏夹名称
        if (collectionName != null) {
            collectionName = collectionName.trim();
            if (collectionName.isEmpty()) {
                throw new IllegalArgumentException("收藏夹名称不能为空");
            }
            if (collectionName.length() > 20) {
                throw new IllegalArgumentException("收藏夹名称不能超过20个字符");
            }
        }

        // 校验描述长度
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("描述不能超过200个字符");
        }

        // 构建动态更新条件
        UpdateWrapper<Collection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("collection_id", collectionId)
                .eq("user_id", userId);

        if (collectionName != null) {
            updateWrapper.set("collection_name", collectionName);
        }
        if (description != null) {
            updateWrapper.set("description", description);
        }
        updateWrapper.set("update_time", new Date());

        int affectedRows = collectionMapper.update(null, updateWrapper);
        if (affectedRows == 0) {
            throw new RuntimeException("更新收藏夹失败");
        }
    }
    @Override
    @Transactional
    public void deleteCollection(Integer userId, Integer collectionId) {
        // 参数基础校验
        if (userId == null || collectionId == null) {
            throw new IllegalArgumentException("用户ID和收藏夹ID不能为空");
        }

        // 查询收藏夹详细信息
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            throw new IllegalArgumentException("收藏夹不存在");
        }

        // 权限校验：确保用户只能删除自己的收藏夹
        if (!collection.getUserId().equals(userId)) {
            throw new IllegalArgumentException("无权删除该收藏夹");
        }

        // 默认收藏夹保护机制
        if (collection.getIsDefault()) {
            throw new IllegalArgumentException("默认收藏夹不可删除");
        }

        //  先删除收藏夹内的所有视频关联
        videoCollectionMapper.deleteByCollectionId(collectionId);

        // 执行删除操作
        int affectedRows = collectionMapper.deleteById(collectionId);
        if (affectedRows == 0) {
            throw new RuntimeException("删除收藏夹失败，可能已被其他操作删除");
        }
    }

    private void validatePageParams(Long pageNum, Long pageSize) {
        if (pageNum == null || pageNum < 1) {
            throw new RuntimeException("页码不能小于1");
        }
        if (pageSize == null || pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
            throw new RuntimeException("每页数量需在1-" + MAX_PAGE_SIZE + "之间");
        }
    }


    private void validateCollection(Integer collectionId) {
        if (collectionId == null || collectionId <= 0) {
            throw new RuntimeException("无效收藏夹ID");
        }

        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null) {
            throw new RuntimeException("收藏夹不存在");
        }
    }

    private void validateParams(Integer userId, Integer n) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        if (n == null || n <= 0) {
            throw new RuntimeException("请求数量必须大于0");
        }
    }

    private void validateUserAndVideo(Integer userId, Integer videoId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        if (videoId == null || videoId <= 0) {
            throw new RuntimeException("无效视频ID");
        }
        // 可选：验证用户和视频存在性
    }

    private void validateUserId(Integer userId) {
        // 基础校验
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }

        // 校验用户存在性
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
    }

    private void validateUserExists(Integer userId) {
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }
    }

    private void validateVideoExists(Integer videoId) {
        if (videoMapper.selectById(videoId) == null) {
            throw new RuntimeException("视频不存在");
        }
    }

    private void validateParams(Integer userId, String collectionName) {
        // 验证用户ID有效性
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }

        // 验证用户存在性
        if (userMapper.selectById(userId) == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证收藏夹名称
        if (StringUtils.isBlank(collectionName)) {
            throw new RuntimeException("收藏夹名称不能为空");
        }
    }

    private String processCollectionName(String rawName) {
        // 去除首尾空格
        String trimmedName = rawName.trim();

        // 长度校验
        if (trimmedName.length() > MAX_COLLECTION_NAME_LENGTH) {
            throw new RuntimeException("收藏夹名称过长");
        }
        return trimmedName;
    }

    private Collection buildCollection(Integer userId, String name, String description) {
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setCollectionName(name);
        collection.setDescription(StringUtils.isNotBlank(description) ? description.trim() : null);
        collection.setIsDefault(false);  // 明确设置为非默认收藏夹
        collection.setCreateTime(new Date());
        collection.setUpdateTime(new Date());
        collection.setVideoCount(0); // 明确初始化视频数量
        return collection;
    }

    private void validateIds(Integer userId, Integer collectionId, Integer videoId) {
        if (userId == null || userId <= 0) {
            throw new RuntimeException("无效用户ID");
        }
        if (collectionId == null || collectionId <= 0) {
            throw new RuntimeException("无效收藏夹ID");
        }
        if (videoId == null || videoId <= 0) {
            throw new RuntimeException("无效视频ID");
        }
    }

    private void updateCollectionCount(Integer collectionId, int delta) {
        int updated = collectionMapper.updateVideoCount(collectionId, delta);
        if (updated == 0) {
            log.error("收藏夹计数更新失败，collectionId: {}");
            throw new RuntimeException("系统错误：收藏夹计数更新失败");
        }
    }

    private void updateVideoCollectionCount(Integer videoId, int delta) {
        int updated = videoMapper.updateCollectionCount(videoId, delta);
        if (updated == 0) {
            log.error("视频收藏计数更新失败，videoId: {}");
            throw new RuntimeException("系统错误：视频计数更新失败");
        }
    }

}
