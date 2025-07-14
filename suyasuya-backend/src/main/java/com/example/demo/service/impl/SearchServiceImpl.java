package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.PageResult;
import com.example.demo.entity.SearchHistory;
import com.example.demo.entity.Video;
import com.example.demo.mapper.SearchHistoryMapper;
import com.example.demo.mapper.VideoMapper;
import com.example.demo.service.SearchService;
import com.example.demo.vo.VideoSearchVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SearchServiceImpl implements SearchService {
    // 常量定义重复时间阈值（单位：分钟）
    @Value("${search.history.interval-minutes:5}") // 冒号后为默认值
    private int searchHistoryInterval;
    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private SearchHistoryMapper searchHistoryMapper; // 新增SearchHistoryMapper依赖

    // 根据关键词查询视频列表
    @Transactional
    @Override
    public PageResult<VideoSearchVO> searchVideos(Integer userId,String keyword, int pageNum, int pageSize,String sortType) {
        // 参数校验
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        validatePageParams(pageNum, pageSize);

        // 根据排序类型设置字段和方向
        String sortField = switch (sortType.toLowerCase()) {
            case "upload_time" -> "upload_time";
            case "collection_count" -> "collection_count";
            default -> "view_count"; // 默认按播放量排序
        };
        String sortOrder = "DESC"; // 统一降序（最新上传/最多收藏）

        // 构建分页参数（MyBatis-Plus分页）
        Page<Video> page = new Page<>(pageNum, pageSize);

        // 执行搜索
        IPage<VideoSearchVO> resultPage = videoMapper.searchPublishedVideos(
                page,
                "%" + keyword.trim() + "%",
                sortType,           // 直接传递原始参数
                "DESC"              // 统一降序
        );

        // 保存搜索记录（新增重复检查逻辑）
        try {
            if (userId != null) {
                String trimmedKeyword = processKeyword(keyword);

                if (!isRecentSearch(userId, trimmedKeyword)) {
                    saveSearchHistory(userId, trimmedKeyword);
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(SearchServiceImpl.class)
                    .error("搜索记录保存失败，关键词：{}，用户ID：{}", keyword, userId, e);
        }

        return new PageResult<>(
                resultPage.getRecords(),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize(),
                resultPage.getPages()
        );
    }
    // 处理关键词格式（trim+长度限制）
    private String processKeyword(String rawKeyword) {
        String trimmed = rawKeyword.trim();
        return trimmed.length() > 255 ? trimmed.substring(0, 255) : trimmed;
    }

    // 检查近期是否存在相同搜索记录
    private boolean isRecentSearch(Integer userId, String keyword) {
        QueryWrapper<SearchHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("keyword", keyword)
                .apply("search_time >= DATE_SUB(NOW(), INTERVAL {0} MINUTE)", searchHistoryInterval);
        return searchHistoryMapper.selectCount(wrapper) > 0;
    }

    // 保存搜索记录到数据库
    private void saveSearchHistory(Integer userId, String keyword) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setSearchTime(new Date());
        searchHistoryMapper.insert(history);
    }

    private void validatePageParams(int pageNum, int pageSize) {
        if (pageNum < 1) {
            throw new IllegalArgumentException("页码参数错误");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("每页数量限制1-100");
        }
    }
}