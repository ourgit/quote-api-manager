package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import security.AdminSecured;

/**
 * BaseAdminController 管理员控制器
 *
 * @link BaseSecurityController
 */
@Security.Authenticated(AdminSecured.class)
public class BaseAdminSecurityController extends BaseAdminController {

    public static final String CODE = "code";
    public static final int CODE200 = 200;
    public static final int CODE403 = 403;
    public static final int CODE404 = 404;
    public static final int CODE500 = 500;
    public static final int CODE40001 = 40001;
    public static final int CODE40002 = 40002;
    public static final int CODE40003 = 40003;
    public static final int CODE40004 = 40004;
    public static final int CODE40005 = 40005;
    public static final int CODE40006 = 40006;
    public static final int CODE40007 = 40007;
    public static final int CODE40008 = 40008;


    /**
     * 未授权
     *
     * @return
     */
    public Result unauth403() {
        ObjectNode node = Json.newObject();
        node.put("code", 403);
        node.put("reason", "未授权");
        return ok(node);
    }

    /**
     * 返回200
     *
     * @return
     */
    public Result okJSON200() {
        ObjectNode node = Json.newObject();
        node.put("code", 200);
        return ok(node);
    }

    public Result okCustomJson(int code, String reason) {
        ObjectNode node = Json.newObject();
        node.put("code", code);
        node.put("reason", reason);
        return ok(node);
    }

    /**
     * 自定义返回json对象的节点
     *
     * @param code
     * @param reason
     * @return
     */
    public ObjectNode customObjectNode(int code, String reason) {
        ObjectNode node = Json.newObject();
        node.put("code", code);
        node.put("reason", reason);
        return node;
    }
}
