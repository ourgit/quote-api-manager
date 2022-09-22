package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.DB;
import models.shop.ShopProductCategory;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.ValidationUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * BaseController 所有的业务逻辑必须都继续自该controller以便方便管理
 *
 * @link BaseSecurityController
 */
public class TestController extends BaseController {
    Logger.ALogger logger = Logger.of(TestController.class);

    public Result index() {
        ObjectNode node = Json.newObject();
        node.put("code", 200);
        node.put("result", "success");
        return ok(node);
    }


    public CompletionStage<Result> geneCategoryPathName() {
        return CompletableFuture.supplyAsync(() -> {
            List<ShopProductCategory> list = ShopProductCategory.find.all();
            Map<Long, String> map = new HashMap<>();
            list.parallelStream().forEach((each) -> {
                map.put(each.id, each.name);
            });
            long currentTime = dateUtils.getCurrentTimeBySecond();
            list.parallelStream().forEach((each) -> {
                String[] pathList = each.path.split("/");
                StringBuilder sb = new StringBuilder();
                sb.append("/");
                Arrays.stream(pathList).forEach((eachPath) -> {
                    if (!ValidationUtil.isEmpty(eachPath)) {
                        if (eachPath.equalsIgnoreCase("/")) sb.append(eachPath);
                        else {
                            Long id = Long.parseLong(eachPath);
                            String result = map.get(id);
                            sb.append(result).append("/");
                        }
                    }
                });
                each.setPathName(sb.toString());
                each.setCreateTime(currentTime);
            });
            DB.saveAll(list);
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.put("result", "success");
            return ok(node);
        });
    }

    public CompletionStage<Result> geneBarcode() {
        return CompletableFuture.supplyAsync(() -> {
            ObjectNode node = Json.newObject();
            node.put("code", 200);
            node.put("result", "success");
            return ok(node);
        });
    }


}
