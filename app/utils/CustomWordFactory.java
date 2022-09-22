package utils;

import com.github.bingoohuang.patchca.word.RandomWordFactory;
import com.github.bingoohuang.patchca.word.WordBean;

import java.util.Random;

/**
 * Created by win7 on 2016/8/24.
 */

public class CustomWordFactory extends RandomWordFactory {

    protected String wideCharacters;

    public void setWideCharacters(String wideCharacters) {
        this.wideCharacters = wideCharacters;
    }

    public CustomWordFactory() {
        characters = "absdegkmnpwx23456789";
        minLength = 4;
        maxLength = 4;
        wideCharacters = "mw";
    }

    @Override
    public WordBean getNextWord() {
        Random rnd = new Random();
        StringBuffer sb = new StringBuffer();
        StringBuffer chars = new StringBuffer(characters);
        int l = minLength + (maxLength > minLength ? rnd.nextInt(maxLength - minLength) : 0);
        for (int i = 0; i < l; i++) {
            int j = rnd.nextInt(chars.length());
            char c = chars.charAt(j);
            if (wideCharacters.indexOf(c) != -1) {
                for (int k = 0; k < wideCharacters.length(); k++) {
                    int idx = chars.indexOf(String.valueOf(wideCharacters.charAt(k)));
                    if (idx != -1) {
                        chars.deleteCharAt(idx);
                    }
                }
            }
            sb.append(c);
        }
        String answer = sb.toString();

        return new WordBean(answer, answer, "请输入图片中的文字");
    }
}
