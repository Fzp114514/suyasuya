package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VideoTagMapper extends BaseMapper<VideoTag> {
    @Select("SELECT * FROM video_tags WHERE tag_name = #{tagName}")
    VideoTag selectByName(String tagName);

    // 查询视频标签
    @Select("SELECT t.tag_name FROM video_tags t " +
            "INNER JOIN video_tag_relations r ON t.tag_id = r.tag_id " +
            "WHERE r.video_id = #{videoId}")
    List<String> selectTagsByVideoId(Integer videoId);

    // 标签热度更新
    @Update("UPDATE video_tags SET related_count = related_count + 1 " +
            "WHERE tag_id = #{tagId}")
    int incrementRelatedCount(Integer tagId);
}
