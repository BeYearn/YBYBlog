package ssm.blog.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ssm.blog.entity.BlogType;
import ssm.blog.entity.PageBean;
import ssm.blog.service.BlogTypeService;
import ssm.blog.util.ResponseUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/admin/blogType")
public class BlogTypeAdminController {

    @Resource
    private BlogTypeService blogTypeService;

    @RequestMapping("/list")
    public void listBlogType(@RequestParam(value = "page", required = false) String page,   //第几页
                             @RequestParam(value = "rows", required = false) String rows,   //一页几行
                             HttpServletResponse response) throws Exception {

        //定义分页bean
        PageBean<BlogType> pageBean = new PageBean<BlogType>(Integer.parseInt(page), Integer.parseInt(rows));

        //拿到已经记录总数的分页结果pageBean
        pageBean = blogTypeService.listByPage(pageBean);

        //使用阿里巴巴的fastJson创建JSONObject
        JSONObject result = new JSONObject();

        //通过fastJson序列化list为jsonArray
        String jsonArray = JSON.toJSONString(pageBean.getResult());
        JSONArray array = JSONArray.parseArray(jsonArray);

        //将序列化结果放入json对象中
        result.put("rows", array);
        result.put("total", pageBean.getTotal());

        //使用自定义工具类向response中写入数据
        ResponseUtil.write(response, result);

        /**
         * 如果此处直接返回 return result; 而方法返回值签名处不加@ResponseBody的话会404 会被认为list.jsp
         *
         */
    }

}
