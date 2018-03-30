package ssm.blog.service;

import ssm.blog.entity.BlogType;
import ssm.blog.entity.PageBean;

public interface BlogTypeService {

    //分页查询
    PageBean<BlogType> listByPage(PageBean<BlogType> pageBean);
}
