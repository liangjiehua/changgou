/**
 * 校验手机号
 */
function checkPhone(phone) {
    var reg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!reg.test(phone)){
        return false;
    }else {
        return true;
    }
}

/**
 * 倒计时
 */
var clock = '';//定时器对象，用于页面30秒倒计时效果
var nums = 30;
var validateCodeButton;
//基于定时器实现30秒倒计时效果
function doLoop() {
    validateCodeButton.disabled = true;//将按钮置为不可点击
    nums--; // 29
    if (nums > 0) {
        validateCodeButton.value = nums + '秒后重新获取'; // 29秒后重新获取
    } else {
        clearInterval(clock); //清除js定时器(clock = window.setInterval：定时)
        validateCodeButton.disabled = false;
        validateCodeButton.value = '重新获取验证码';
        nums = 30; //重置时间
    }
}
