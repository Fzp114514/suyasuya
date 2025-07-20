package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.CollectionUpdateRequest;
import com.example.demo.entity.UserCollection;
import com.example.demo.service.CollectionService;
import com.example.demo.vo.CollectedVideoVO;
import com.example.demo.vo.CollectionVideoVO;
import com.example.demo.vo.UserCollectionVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;


    // 创建收藏夹接口
    @PostMapping("/create")
    public Result<UserCollection> createCollection(@RequestAttribute Integer userId, // 从拦截器中获取的 userId
                                                   @RequestParam String collectionName,
                                                   @RequestParam(required = false) String description) {
        try {
            // 调用服务方法创建收藏夹
            UserCollection userCollection = collectionService.createCollection(userId, collectionName, description);

            // 返回成功结果
            return Result.success("创建收藏夹成功", userCollection);
        } catch (IllegalArgumentException e) {
            // 捕获业务异常并返回错误信息
            return Result.failure("已存在同名收藏夹");
        } catch (Exception e) {
            // 捕获其他异常并返回错误信息
            return Result.failure("创建收藏夹失败，请稍后再试" + e.getMessage());
        }
    }

    /**
     * 获取指定用户的所有收藏夹（按创建时间升序排序）
     *
     * @param userId 用户ID
     * @return 包含所有收藏夹的响应
     */
    @GetMapping("/user")
    public Result<List<UserCollection>> getUserCollections(@RequestAttribute Integer userId) {
        try {
            // 调用 service 方法获取用户的所有收藏夹
            List<UserCollection> userCollections = collectionService.getUserCollections(userId);

            // 返回成功的响应
            return Result.success("获取用户收藏夹成功", userCollections);
        } catch (Exception e) {
            // 捕获异常，返回失败的响应
            return Result.failure("获取用户收藏夹失败: " + e.getMessage());
        }
    }


    /**
     * 将视频添加到指定收藏夹
     *
     * @param collectionId 收藏夹ID
     * @param videoId      视频ID
     * @return 返回操作结果
     */
    @PostMapping("/addVideo")
    public Result<String> addVideoToCollection(@RequestAttribute Integer userId, @RequestParam Integer collectionId, @RequestParam Integer videoId) {
        try {
            // 调用服务方法将视频添加到收藏夹
            collectionService.addVideoToCollection(userId, collectionId, videoId);
            // 返回成功的响应
            return Result.success("收藏视频成功");
        } catch (RuntimeException e) {
            // 捕获业务异常，返回失败的响应
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，返回通用失败响应
            return Result.failure("操作失败，请稍后再试");
        }
    }

    // 检查视频收藏状态
    @GetMapping("/check")
    public Result<List<UserCollectionVO>> checkVideoCollections(
            @RequestAttribute Integer userId,
            @RequestParam Integer videoId
    ) {
        try {
            List<UserCollectionVO> data = collectionService.getVideoCollectionsByUser(userId, videoId);
            return data.isEmpty() ?
                    Result.success("视频未被收藏", data) :
                    Result.success("视频收藏信息查询成功", data);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("系统繁忙，请稍后再试" + e.getMessage());
        }
    }

    // 从收藏夹中删除视频
    @DeleteMapping("/deleteVideo")
    public Result<?> removeVideoFromCollection(
            @RequestAttribute Integer userId,
            @RequestParam @Min(1) Integer collectionId,
            @RequestParam @Min(1) Integer videoId
    ) {
        try {
            collectionService.removeVideoFromCollection(userId,collectionId, videoId);
            return Result.success("视频已从收藏夹移除");
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("移除视频失败，请稍后再试" + e.getMessage());
        }
    }

    // 获取用户最近收藏的视频
    @GetMapping("/getRecentVideos")
    public Result<List<CollectedVideoVO>> getRecentCollectedVideos(
            @RequestParam Integer userId,
            @RequestParam @Min(1) @Max(100) Integer n
    ) {
        try {
            List<CollectedVideoVO> data = collectionService
                    .getRecentCollectedVideos(userId, n);
            return Result.success("查询成功", data);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }

    @GetMapping("/getVideos/{collectionId}")
    public Result<PageResult<CollectionVideoVO>> getCollectionVideos(
            @PathVariable Integer collectionId,
            @RequestParam(defaultValue = "1") Long pageNum,
            @RequestParam(defaultValue = "10") Long pageSize
    ) {
        try {
            PageResult<CollectionVideoVO> result = collectionService
                    .getCollectionVideosPage(collectionId, pageNum, pageSize);
            return Result.success("分页查询成功", result);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }
    @PostMapping("/update")
    public ResponseEntity<Result<?>> updateCollection(@RequestAttribute Integer userId,@RequestBody CollectionUpdateRequest request) {
        try {
            collectionService.updateCollection(userId, request.getCollectionId(),
                    request.getCollectionName(), request.getDescription());
            return ResponseEntity.ok(Result.success("收藏夹更新成功"));
        } catch (IllegalArgumentException e) {
            // 业务逻辑校验失败（400 Bad Request）
            return ResponseEntity.badRequest()
                    .body(Result.failure(e.getMessage()));
        } catch (Exception e) {
            // 系统异常（500 Internal Server Error）
            return ResponseEntity.internalServerError()
                    .body(Result.failure("服务器繁忙，请稍后重试"));
        }
    }
    @DeleteMapping("/deleteCollection/{collectionId}")
    public ResponseEntity<Result<?>> deleteCollection(
            @PathVariable Integer collectionId,
            @RequestAttribute Integer userId // 从拦截器中获取的 userId
    ) {
        try {
            collectionService.deleteCollection(userId, collectionId);
            return ResponseEntity.ok(Result.success("收藏夹删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Result.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Result.failure("服务器繁忙，请稍后重试"));
        }
    }
}
