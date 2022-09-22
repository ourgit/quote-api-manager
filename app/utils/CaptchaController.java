package utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.bingoohuang.patchca.color.SingleColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.predefined.WobbleRippleFilterFactory;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import controllers.BaseController;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by win7 on 2016/8/5.
 */
public class CaptchaController extends BaseController {
    ObjectNode result = Json.newObject();
    Logger.ALogger logger = Logger.of(CaptchaController.class);
    public Result getCaptcha() {
        ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
        cs.setWordFactory(new CustomWordFactory());
        cs.setWidth(100);
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setFilterFactory(new WobbleRippleFilterFactory());
        FileOutputStream fos = null;
        try {
            String today = dateUtils.getFormatdayFarfromToday(0);
            File dir = new File("/tmp/play/" + today);
            if (!dir.exists()) {
                FileUtils.forceMkdir(dir);
            }
            String fileName = dir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
            fos = new FileOutputStream(fileName);
            EncoderHelper.getChallangeAndWriteImage(cs, "png", fos);
            return ok(new File(fileName));
        } catch (IOException e) {
            logger.error("getCaptcha" + e.getMessage());
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result.put(CODE, CODE200);
        return ok(result);
    }

    private void setFactory() {
        //            cs.setFilterFactory(new DiffuseRippleFilterFactory());
//            switch (counter % 5) {
//                case 0:
//                    cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
//                    break;
//                case 1:
//                    cs.setFilterFactory(new MarbleRippleFilterFactory());
//                    break;
//                case 2:
//                    cs.setFilterFactory(new DoubleRippleFilterFactory());
//                    break;
//                case 3:
//                    cs.setFilterFactory(new WobbleRippleFilterFactory());
//                    break;
//                case 4:
//                    cs.setFilterFactory(new DiffuseRippleFilterFactory());
//                    break;
//            }
    }
}
