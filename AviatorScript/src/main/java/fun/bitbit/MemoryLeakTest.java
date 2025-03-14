package fun.bitbit;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 验证 5.3.2版本中的内存泄露问题 https://github.com/killme2008/aviatorscript/issues/481
 * -Xms1024M -Xmx1024M -XX:MaxMetaspaceSize=256M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=aviactorscript_leak.hprof
 * Date: 2024/6/23 6:08
 */
public class MemoryLeakTest {
    public static void main(String[] args) throws InterruptedException {
        String express = "let a = math.pow((math.abs(c)+a*10)-100,2)+0.2;if(b==45){return a;}else {return -0.7;}";

        while (true) {
            Expression expression = AviatorEvaluator.compile(express);
            Map<String, Object> env = new HashMap<>();
            env.put("a", 100.3);
            env.put("b", 45);
            env.put("c", -199.100);
            Thread.sleep(1);
            Object result = expression.execute(env);
        }
    }
}
