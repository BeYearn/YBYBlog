package ssm.blog.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import ssm.blog.entity.BlogType;

import java.util.List;

/**
 * Created by xp on 2017/4/14.
 *
 * @author xp
 * @Description 博客类别dao
 */
@Repository
public interface BlogTypeDao {

    /**
     * 添加博客类别信息
     *
     * @param blogType
     * @return
     */
    Integer addBlogType(BlogType blogType);

    /**
     * 删除博客类别信息
     *
     * @param id
     * @return
     */
    Integer deleteBlogType(Integer id);

    /**
     * 更新博客类别信息
     *
     * @param blogType
     * @return
     */
    Integer updateBlogType(BlogType blogType);

    /**
     * 根据id查询博客类别信息
     *
     * @param id
     * @return
     */
    BlogType getById(Integer id);

    /**
     * 分页查询博客类别信息
     *          - 两个参数分别是 start 与 end 其中对应 sql语句中的 limit start,end.
                - 在我们分页中 start对应的(page-1)✖pageSize ,end对应的是page✖pageSize

     - 由于是两个参数 无法使用parameterType 但是mybatis给我们提供了两种方法
        1 封装成Map对象
        2使用@Param(value=”name”)注解 默认value=可以省略也就是这样子 @Param(“name”)在这里我使用@Param注解 这样我们在mapper文件中就可以直接通过#{name}获取参数中的值

     * @param start
     * @param end
     * @return
     */
    List<BlogType> listByPage(@Param("start") Integer start, @Param("end") Integer end);

    /**
     * 查询总记录数
     *
     * @return
     */
    Long getTotal();

    /**
     * 获取博客类别信息
     */
    public List<BlogType> getBlogTypeData();
}
