package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Collection;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {
    @Update("UPDATE collections SET video_count = video_count + #{increment} WHERE collection_id = #{collectionId}")
    int updateVideoCount(@Param("collectionId") Integer collectionId,
                         @Param("increment") int increment);

    // 通过用户ID和收藏夹名称查找收藏夹
    @Select("SELECT * FROM collections WHERE user_id = #{userId} AND collection_name = #{collectionName}")
    Collection selectByUserIdAndName(@Param("userId") Integer userId, @Param("collectionName") String collectionName);
}
