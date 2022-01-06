/**
 * （1）未满加水：通过代码 water +=1进行不停加水的动作。
 * （2）漏水：通过时间差来计算漏水量。
 * （3）剩余水量：总水量-漏水量。
 */
    public class LeakyBucket {
        public long timeStamp = System.currentTimeMillis();  // 当前时间
        public long capacity; // 桶的容量
        public long rate; // 水漏出的速度
        public long water; // 当前水量(当前累积请求数)

        public boolean grant() {
            long now = System.currentTimeMillis();
            // 先执行漏水，计算剩余水量
            water = Math.max(0, water - (now - timeStamp) * rate);

            timeStamp = now;
            if ((water + 1) < capacity) {
                // 尝试加水,并且水还未满
                water += 1;
                return true;
            } else {
                // 水满，拒绝加水
                return false;
            }
        }
    }


/**
 * 说明：
 *
 *         （1）令牌发放：这里通过时间差来进行发送令牌。
 *
 *         （2）令牌领取：直接使用-1的方式。
 *
 *         （3）满了丢弃：使用Math.min取最小值，所以最大也就是初始设置的容量。
 */
public class TokenBucket {
    public long timeStamp = System.currentTimeMillis(); // 当前时间
    public long capacity; // 桶的容量
    public long rate; // 令牌放入速度
    public long tokens; // 当前令牌数量

    public boolean grant() {
        long now = System.currentTimeMillis();
        // 先添加令牌
        tokens = Math.min(capacity, tokens + (now - timeStamp) * rate);
        timeStamp = now;
        if (tokens < 1) {
            // 若不到1个令牌,则拒绝
            return false;
        } else {
            // 还有令牌，领取令牌
            tokens -= 1;
            return true;
        }
    }
}
