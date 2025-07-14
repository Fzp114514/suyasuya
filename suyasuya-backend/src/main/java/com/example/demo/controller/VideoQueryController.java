package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.dto.VideoDTO;
import com.example.demo.service.VideoQueryService;
import com.example.demo.vo.VideoSimpleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoQueryController {
    @Autowired
    private VideoQueryService videoQueryService;

    // 随机推荐接口
    @GetMapping("/random")
    public Result<List<VideoSimpleVO>> getRandomVideos(
            @RequestParam(defaultValue = "10") Integer count
    ) {
        try {
            List<VideoSimpleVO> videos = videoQueryService.getRandomVideos(count);
            return Result.success("推荐视频获取成功", videos);
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    // 分类视频接口
    @GetMapping("/category/{categoryId}")
    public Result<List<VideoSimpleVO>> getCategoryVideos(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        try {
            List<VideoSimpleVO> videos = videoQueryService.getVideosByCategory(categoryId, count);
            return Result.success("分类视频获取成功", videos);
        } catch (IllegalArgumentException e) {
            return Result.failure(e.getMessage());
        }
    }

    /**
     * 根据用户ID获取按上传时间排序的已发布视频列表
     *
     * @param userId 用户ID
     * @return 返回按上传时间排序的视频列表
     */
    @PostMapping("/getVideosByUserByUploadTime")
    public Result<Page<VideoDTO>> getVideosByUserByUploadTime(@RequestParam Integer userId,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 调用 service 获取分页结果
            Page<VideoDTO> videos = videoQueryService.getVideosByUserByUploadTime(userId, pageNum, pageSize);
            return Result.success("获取视频列表成功", videos);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PostMapping("/getVideosByUserByViewCount")
    public Result<Page<VideoDTO>> getVideosByUserByViewCount(@RequestParam Integer userId,
                                                             @RequestParam(defaultValue = "1") int pageNum,
                                                             @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 调用 service 获取分页结果
            Page<VideoDTO> videos = videoQueryService.getVideosByUserByViewCount(userId, pageNum, pageSize);
            return Result.success("获取视频列表成功", videos);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }

    @PostMapping("/getVideosByUserByCollectionCount")
    public Result<Page<VideoDTO>> getVideosByUserByCollectionCount(@RequestParam Integer userId,
                                                                   @RequestParam(defaultValue = "1") int pageNum,
                                                                   @RequestParam(defaultValue = "10") int pageSize) {
        try {
            // 调用 service 获取分页结果
            Page<VideoDTO> videos = videoQueryService.getVideosByUserByCollectionCount(userId, pageNum, pageSize);
            return Result.success("获取视频列表成功", videos);
        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        }
    }
}
