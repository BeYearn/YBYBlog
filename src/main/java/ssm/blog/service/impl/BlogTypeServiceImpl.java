package ssm.blog.service.impl;

import org.springframework.stereotype.Service;
import ssm.blog.dao.BlogTypeDao;
import ssm.blog.entity.BlogType;
import ssm.blog.entity.PageBean;
import ssm.blog.service.BlogTypeService;

import javax.annotation.Resource;


@Service
public class BlogTypeServiceImpl implements BlogTypeService {

    @Resource
    private BlogTypeDao blogTypeDao;

    public PageBean<BlogType> listByPage(PageBean<BlogType> pageBean) {

        //查询分页结果
        pageBean.setResult(blogTypeDao.listByPage(pageBean.getStart(), pageBean.getEnd()));
        //查询记录总数
        pageBean.setTotal(blogTypeDao.getTotal());

        return pageBean;
    }
}
