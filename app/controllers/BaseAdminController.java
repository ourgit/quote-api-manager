package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.cache.AsyncCacheApi;
import play.cache.SyncCacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import security.CorsAction;
import utils.BizUtils;
import utils.CacheUtils;
import utils.DateUtils;

import javax.inject.Inject;

/**
 * BaseAdminController 管理员控制器
 *
 * @link BaseSecurityController
 */
@With(CorsAction.class)
public class BaseAdminController extends Controller {

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

    @Inject
    protected DateUtils dateUtils;
    @Inject
    protected BizUtils businessUtils;
    @Inject
    protected CacheUtils cacheUtils;
    @Inject
    protected SyncCacheApi syncCache;
    @Inject
    protected AsyncCacheApi asyncCache;


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
