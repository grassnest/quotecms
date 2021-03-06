package com.atk.module.web.cms;

import com.atk.module.web.cms.service.ItemService;
import com.atk.module.web.cms.service.PatternService;
import com.atk.mybatis.model.TCmsItem;
import com.atk.mybatis.model.TCmsPattern;
import com.zhiliao.common.annotation.SysLog;
import com.zhiliao.common.utils.CmsUtil;
import com.zhiliao.common.utils.HtmlKit;
import com.zhiliao.common.utils.PinyinUtil;
import com.zhiliao.common.utils.UserUtil;
import com.zhiliao.module.web.system.service.RoleService;
import com.zhiliao.module.web.system.service.SysUserService;
import com.zhiliao.module.web.system.vo.UserVo;
import com.zhiliao.mybatis.model.TSysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/system/cms/item")
public class ItemController {
    @Autowired
    private PatternService patternService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RoleService roleService;

    @RequiresPermissions("item:admin")
    @RequestMapping
    public String index(){
        return "data/item";
    }

    @SysLog("品种添加")
    @RequiresPermissions("item:input")
    @RequestMapping("/input")
    public String input(@RequestParam(value = "id",required = false) Long Id, Model model) {
        List<TCmsPattern> patterns = patternService.findPatternListByStatus(true);
        model.addAttribute("models",patterns);
        if(Id!=null)
            model.addAttribute("pojo",itemService.findById(Id));
        return "data/item_input";
    }


    @RequiresPermissions("item:save")
    @RequestMapping("/save")
    @ResponseBody
    public String save(TCmsItem pojo) throws SQLException {
        if(pojo.getHasChild() == null) {
            pojo.setHasChild(false);
        }
        UserVo userVo = UserUtil.getSysUserVo();
        pojo.setSiteId(userVo.getSiteId());
        if(pojo.getItemId()!=null)
            return  itemService.update(pojo);
        return itemService.save(pojo);
    }

    @RequestMapping("/toUser")
    public String toUser(@RequestParam(value = "userId",required = false) Integer userId, Model model) {
        if(userId!=null) {
            model.addAttribute("user", sysUserService.findSysUserByUserId(userId));
            model.addAttribute("userRole", roleService.findByUserId(userId));
        }
        model.addAttribute("items", itemService.findItemListByPid(0l));
        return "data/user_item";
    }

    @RequiresPermissions({"userItem:save"})
    @RequestMapping("/saveToUser")
    @ResponseBody
    public String saveToUser(TSysUser user, @RequestParam(value = "itemId",required = false) Long[] itemId) {
        if(user.getUserId() != null) {
            return itemService.update(user, itemId);
        }
        return "保存成功";
    }

    @RequestMapping("/checkItem")
    @ResponseBody
    public String checkDomain(@RequestParam(value = "itemName",required = false) String itemName){
        if(!CmsUtil.isNullOrEmpty(itemService.findByAlias(PinyinUtil.convertLower(HtmlKit.getText(itemName)))))
            return "{\"error\": \"品种已存在\"}";
        return "{\"ok\": \"验证通过\"}";

    }
}
