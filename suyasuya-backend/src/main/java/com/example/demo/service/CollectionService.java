package com.example.demo.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.common.PageResult;
import com.example.demo.entity.UserCollection;
import com.example.demo.vo.CollectedVideoVO;
import com.example.demo.vo.CollectionVideoVO;
import com.example.demo.vo.UserCollectionVO;

import java.util.List;

public interface CollectionService extends IService<UserCollection> {
    UserCollection createCollection(Integer userId, String collectionName, String description);
    List<UserCollection> getUserCollections(Integer userId);
    void addVideoToCollection(Integer userId, Integer collectionId, Integer videoId);
    List<UserCollectionVO> getVideoCollectionsByUser(Integer userId, Integer videoId);
    void removeVideoFromCollection(Integer userId, Integer collectionId, Integer videoId);
    List<CollectedVideoVO> getRecentCollectedVideos(Integer userId, Integer n);
    PageResult<CollectionVideoVO> getCollectionVideosPage(Integer collectionId, Long pageNum, Long pageSize);
    void updateCollection(Integer userId, Integer collectionId, String collectionName, String description);
    void deleteCollection(Integer userId, Integer collectionId);
}
