package controllers.stat;

import com.fasterxml.jackson.databind.node.ObjectNode;
import constants.BusinessConstant;
import controllers.BaseAdminSecurityController;
import io.ebean.ExpressionList;
import io.ebean.PagedList;
import models.stat.*;
import play.libs.Json;
import play.mvc.Result;
import utils.ValidationUtil;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 统计管理
 */
public class StatManager extends BaseAdminSecurityController {

    /**
     * @api {GET} /v1/cp/reg_stat/?page= 01注册统计数据列表
     * @apiName listRegStatistics
     * @apiGroup Admin-STATISTICS
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {int} pages 页数
     * @apiSuccess (Success 200) {JsonArray} list 列表
     * @apiSuccess (Success 200) {string} date 日期
     * @apiSuccess (Success 200) {long} regCount 注册用户数
     * @apiSuccess (Success 200) {long} total 总用户数
     */
    public CompletionStage<Result> listReg(int page) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<StatisticsReg> expressionList = StatisticsReg.find.query().where();
            PagedList<StatisticsReg> pagedList = expressionList.orderBy().desc("id")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_10)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_10)
                    .findPagedList();
            List<StatisticsReg> list = pagedList.getList();
            int pages = pagedList.getTotalPageCount();
            ObjectNode node = Json.newObject();
            node.put(CODE, CODE200);
            node.put("pages", pages);
            node.set("list", Json.toJson(list));
            return ok(node);
        });
    }

    /**
     * @api {GET} /v1/cp/platform_total_stat/ 02平台总销量统计
     * @apiName listTotalStat
     * @apiGroup Admin-STATISTICS
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {string} commission 抽成
     * @apiSuccess (Success 200) {long} orderAmount 订单总额
     */
    public CompletionStage<Result> listTotalStat() {
        return CompletableFuture.supplyAsync(() -> {
            StatTotalOverview totalOverview = StatTotalOverview.find.query().where().setMaxRows(1).findOne();
            if (null == totalOverview) {
                totalOverview = new StatTotalOverview();
                totalOverview.setCommission(0);
                totalOverview.setOrderAmount(0);
                totalOverview.setCreatedTime(dateUtils.getCurrentTimeBySecond());
                totalOverview.save();
            }
            ObjectNode result = (ObjectNode) Json.toJson(totalOverview);
            result.put("orderAmount", NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(totalOverview.orderAmount));
            result.put(CODE, CODE200);
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/platform_month_stat/?month=&page=&beginDate=&endDate=&shopId= 03平台月销量统计
     * @apiName listPlatformMonthStat
     * @apiGroup Admin-STATISTICS
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {string} commission 抽成
     * @apiSuccess (Success 200) {string} month 月份
     * @apiSuccess (Success 200) {long} orderAmount 订单总额
     */
    public CompletionStage<Result> listPlatformMonthStat(String month, int page, long beginDate, long endDate,long shopId) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<StatPlatformMonthSalesOverview> expressionList = StatPlatformMonthSalesOverview.find.query().where()
                    .eq("shopId",shopId);
            if (!ValidationUtil.isEmpty(month)) expressionList.eq("month", month);
            if (beginDate > 0) expressionList.ge("createdTime", beginDate);
            if (endDate > 0) expressionList.le("createdTime", endDate);
            PagedList<StatPlatformMonthSalesOverview> pagedList = expressionList
                    .orderBy().desc("month")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            List<StatPlatformMonthSalesOverview> list = pagedList.getList();
            list.forEach((each) -> {
                each.setTotalMoneyStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.totalMoney));
                each.setTotalCommissionStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.totalCommission));
                each.setPlatformCommissonStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.platFormCommission));
            });
            int pages = pagedList.getTotalPageCount();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/platform_day_stat/?day=&page=&beginDate=&endDate=&shopId= 04平台日销量统计
     * @apiName listPlatformDayStat
     * @apiGroup Admin-STATISTICS
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {string} commission 抽成
     * @apiSuccess (Success 200) {string} day 月份
     * @apiSuccess (Success 200) {long} orderAmount 订单总额
     */
    public CompletionStage<Result> listPlatformDayStat(String day, int page, long beginDate, long endDate,long shopId) {
        return CompletableFuture.supplyAsync(() -> {
            ExpressionList<StatPlatformDaySalesOverview> expressionList = StatPlatformDaySalesOverview.find.query().where()
                    .eq("shopId",shopId);
            if (!ValidationUtil.isEmpty(day)) expressionList.eq("day", day);
            if (beginDate > 0) expressionList.ge("createdTime", beginDate);
            if (endDate > 0) expressionList.le("createdTime", endDate);
            PagedList<StatPlatformDaySalesOverview> pagedList = expressionList
                    .orderBy().desc("day")
                    .setFirstRow((page - 1) * BusinessConstant.PAGE_SIZE_20)
                    .setMaxRows(BusinessConstant.PAGE_SIZE_20)
                    .findPagedList();
            List<StatPlatformDaySalesOverview> list = pagedList.getList();
            list.forEach((each) -> {
                each.setTotalMoneyStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.totalMoney));
                each.setTotalCommissionStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.totalCommission));
                each.setPlatformCommissonStr(NumberFormat.getCurrencyInstance(Locale.SIMPLIFIED_CHINESE).format(each.platFormCommission));
            });
            int pages = pagedList.getTotalPageCount();
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("pages", pages);
            result.set("list", Json.toJson(list));
            return ok(result);
        });
    }

    /**
     * @api {GET} /v1/cp/home_page_stat/?shopId= 11首页统计
     * @apiName homepageStat
     * @apiGroup Admin-STATISTICS
     * @apiSuccess (Success 200) {int} code 200 请求成功
     * @apiSuccess (Success 200) {long} regCount 今日注册人数
     * @apiSuccess (Success 200) {long} orders 今日订单数
     * @apiSuccess (Success 200) {long} totalMoney 今日订单总额
     * @apiSuccess (Success 200) {long} products 今日售出商品数
     * @apiSuccess (Success 200) {JsonArray} list 最新30条统计数据
     */
    public CompletionStage<Result> homepageStat(long shopId) {
        return CompletableFuture.supplyAsync(() -> {
            List<StatPlatformDaySalesOverview> dayStatList = StatPlatformDaySalesOverview.find.query().where()
                    .eq("shopId",shopId)
                    .orderBy().desc("id")
                    .setMaxRows(30)
                    .findList();
            dayStatList.parallelStream().forEach((each) -> {
                if (!ValidationUtil.isEmpty(each.getDay()) && each.getDay().length() >= 8)
                    each.setDay(each.getDay().substring(5));
            });
            long todayOrders = 0;
            double todayOrderMoney = 0;
            long todaySoldProducts = 0;
            long todayRegCount = 0;
            if (dayStatList.size() > 0) {
                StatPlatformDaySalesOverview todayOverview = dayStatList.get(0);
                todayOrders = todayOverview.orders;
                todayOrderMoney = todayOverview.totalMoney;
                todaySoldProducts = todayOverview.products;
                todayRegCount = todayOverview.regCount;
            }

            String currentMonth =  dateUtils.getCurrentMonth();
            StatPlatformMonthSalesOverview monthSalesOverview = StatPlatformMonthSalesOverview.find.query().where()
                    .eq("shopId",shopId)
                    .eq("month",currentMonth)
                    .orderBy().asc("id")
                    .setMaxRows(1)
                    .findOne();
            long monthOrders = 0;
            double monthOrderMoney = 0;
            long monthSoldProducts = 0;
            long monthRegCount = 0;
            if(null != monthSalesOverview){
                monthOrders = monthSalesOverview.orders;
                monthOrderMoney = monthSalesOverview.totalMoney;
                monthRegCount = monthSalesOverview.regCount;
                monthSoldProducts = monthSalesOverview.products;
            }
            ObjectNode result = Json.newObject();
            result.put(CODE, CODE200);
            result.put("regCount", todayRegCount);
            result.put("orders", todayOrders);
            result.put("totalMoney", todayOrderMoney);
            result.put("products", todaySoldProducts);

            result.put("monthOrders", monthOrders);
            result.put("monthOrderMoney", monthOrderMoney);
            result.put("monthSoldProducts", monthSoldProducts);
            result.put("monthRegCount", monthRegCount);

            result.set("dayStatList", Json.toJson(dayStatList));
            return ok(result);
        });
    }


}
