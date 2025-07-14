package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.VideoPlaybackService;
import com.example.demo.vo.RecommendVideoVO;
import com.example.demo.vo.VideoDetailVO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoPlaybackController {
    @Autowired
    private VideoPlaybackService videoPlaybackService;

    /**
     * 获取视频详细信息
     * @param videoId 路径参数视频ID
     */
    @GetMapping("/detail/{videoId}")
    public Result<VideoDetailVO> getVideoDetail(@PathVariable Integer videoId) {
        try {
            // 参数校验
            if (videoId == null || videoId <= 0) {
                return Result.failure("视频ID格式错误");
            }

            VideoDetailVO detail = videoPlaybackService.getVideoDetail(videoId);
            return Result.success("视频详情获取成功", detail);

        } catch (RuntimeException e) {
            // 业务异常（如视频不存在）
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            // 系统异常
            return Result.failure("视频详情获取失败，请稍后重试");
        }
    }

    /**
     * 获取视频标签列表
     * @param videoId 路径参数视频ID
     */
    @GetMapping("/tags/{videoId}")
    public Result<List<String>> getVideoTags(@PathVariable Integer videoId) {
        try {
            if (videoId == null || videoId <= 0) {
                return Result.failure("视频ID格式错误");
            }

            List<String> tags = videoPlaybackService.getVideoTags(videoId);
            return Result.success("标签获取成功", tags);

        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("标签获取失败，请稍后重试");
        }
    }

    /**
     * 获取推荐视频列表
     * @param videoId 路径参数视频ID
     */
    @GetMapping("/recommend/{videoId}")
    public Result<List<RecommendVideoVO>> getRelatedRecommendVideos(
            @PathVariable Integer videoId) {
        try {
            if (videoId == null || videoId <= 0) {
                return Result.failure("视频ID格式错误");
            }

            List<RecommendVideoVO> recommends =
                    videoPlaybackService.getRelatedRecommendVideos(videoId);
            return Result.success("推荐视频列表获取成功", recommends);

        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("推荐视频列表获取失败");
        }
    }
    /**
     * 更新播放量接口
     * @param videoId 路径参数
     */
    @PostMapping("/views/{videoId}")
    public Result<?> incrementVideoViews(@PathVariable Integer videoId) {
        try {
            if (videoId == null || videoId <= 0) {
                return Result.failure("视频ID不合法");
            }
            // 异步处理播放量统计
            videoPlaybackService.incrementViewCount(videoId);
            return Result.success("播放量统计已接收");

        } catch (RuntimeException e) {
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            return Result.failure("播放量统计失败");
        }
    }
}

