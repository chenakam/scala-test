/*
 * Copyright (C) 2022-present, Chenai Nakam(chenai.nakam@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hobby.chenai.nakam.test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 01/06/2022
 */
public class Solution {
    public static void main(String[] args) {
        String S = "test 5 a0A pass007 ?xy1";
        System.out.println(S);
        System.out.println(new Solution().solution(S));
    }

    // TODO: 英文要求是写一个`solution()`方法，返回数字。与中文要求的输入输出不一样。
    public int solution(String S) {
        int len = S.length();
        if (!(len > 0 && len < 200)) throw new IllegalArgumentException();

        String[] sorted = Arrays.stream(S.split("[ ]+"))
                .sorted((o1, o2) -> Integer.compare(o2.length(), o1.length())).toArray(String[]::new);
        //.filter(this::isValid)
        //.findFirst().map(String::length).orElse(-1);
        for (String word : sorted) {
            if (isValid(word)) return word.length();
        }
        return -1;
    }

    boolean isValid(String word) {
        Matcher m = pattern.matcher(word);
        if (!m.matches()) return false;
        else {
            int lenNum = 0;
            int lenChar = 0;
            char[] chars = word.toCharArray();
            for (char c : chars) {
                if (c >= '0' && c <= '9') lenNum += 1;
                else lenChar += 1;
            }
            return lenNum % 2 == 1 && lenChar % 2 == 0;
        }
    }

    Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
}
