/**
 * 
 * @param url 获取assets静态资源
 * @returns 
 */
const getAssetsFile = (url) => {
  return new URL(`../assets/images/${url}`, import.meta.url).href;
}

/**
 * 手机号校验
 * @param phoneNumber 
 * @returns 
 */
const validatePhoneNumber = (phoneNumber) => {
  const phoneNumberRegex = /^1(3\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\d|9[0-35-9])\d{8}$/;
  return phoneNumberRegex.test(phoneNumber);
}

/**
 * 密码校验
 * 以字母开头，长度在6~18之间，只能包含字母、数字和下划线
 */
const validatePassword = (password) => {
  const passwordRegex = /^[a-zA-Z]\w{5,17}$/;
  return passwordRegex.test(password);
}
/**
 * 邮箱校验
 * 
 */
const validateEmail = (email) => {

  const emaileRegex = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  return emaileRegex.test(email);
}
/**
 * 身份证校验
 * 
 */
const validateCard = (id_card) => {
  const emaileRegex = /^([1-9]\d{5})(\d{4})(\d{2})(\d{2})(\d{3})(\d|X)$/;
  return emaileRegex.test(id_card);
}
/**
 * 校验验证码
 */
const validateCode = (code) => {
  const codeRegex = /^[0-9]\d{5}$/;
  return codeRegex.test(code);
}

/**
 * 是否外链
 * @param link 
 * @returns 
 */
const isExternalLink = (link) => {
  const regex = /^(http|https|ftp):\/\/[^\s/$.?#].[^\s]*$/;
  return regex.test(link);
};


/**
 * 
 * @param phone 手机号脱敏
 * @returns 
 */
const desensitizePhone = (phone) => {
  if (phone && phone.length === 11) {
    return phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');
  } else {
    return phone
  }
}

/**
* 
* @param idCard 身份证号脱敏
* @returns 
*/
const desensitizeIDCard = (idCard) => {
  if (idCard && (idCard.length === 15 || idCard.length === 18)) {
    return idCard.replace(/^(.{6})(?:\d+)(.{4})$/, '$1****$2');
  } else {
    return idCard
  }
}

/**
* 
* @param email 邮箱脱敏
* @returns 
*/
const desensitizeEmail = (email) => {
  const atIndex = email.indexOf('@');
  if (email && atIndex > 0) {
    const prefix = email.substring(0, atIndex);
    const suffix = email.substring(atIndex);
    const maskedPrefix = prefix.length > 3 ? prefix.slice(0, 3) + '*'.repeat(prefix.length - 3) : prefix;
    return maskedPrefix + suffix;
  } else {
    return email;
  }


}
const isNullOrEmpty = (str) => {
  return !str || str.length === 0;
}
/**
* 检测图片
*/
export const isImageValid = (fileName) => {
  const phoneNumberRegex = /.+\.(jpg|jpeg|png)$/;
  return phoneNumberRegex.test(fileName);
}
export const isVideoValid = (fileName) => {
  const phoneNumberRegex = /.+\.(mp4|webm|ogg)$/;
  return phoneNumberRegex.test(fileName);
}
export default {
  getAssetsFile,
  validatePhoneNumber,
  validatePassword,
  validateEmail,
  validateCode,
  isExternalLink,
  desensitizePhone,
  desensitizeEmail,
  desensitizeIDCard,
  isNullOrEmpty,
  isImageValid,
  isVideoValid,
  validateCard
}