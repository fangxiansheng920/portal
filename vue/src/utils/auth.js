import { jwtDecode } from 'jwt-decode';

// 获取存储在 localStorage 中的 token
const getToken = () => {
  return localStorage.getItem('token');
};

// 解码JWT并缓存结果，避免重复解码
let cachedUser = null;

const decodeToken = () => {
  console.log('token:', getToken());
  const token = getToken();
  if (!token) return null;

  // 如果已经解码过，直接使用缓存
  if (cachedUser) return cachedUser;

  try {
    cachedUser = jwtDecode(token);
    return cachedUser;
  } catch (error) {
    console.error('JWT解码失败:', error);
    return null;
  }
};

// 获取当前用户的 user_id
const getCurrentUserId = () => {
  const user = decodeToken();
  return user?.id || null;
};



// 获取当前用户的手机号
const getCurrentPhoneNumber = () => {
  const user = decodeToken();
  return user?.phoneNumber || null;
};

// 获取当前用户的创建时间
const getCurrentCreateTime = () => {
  const user = decodeToken();
  return user?.createTime || null;
};

// 获取当前用户的完整信息
const getCurrentUser = () => {
  return decodeToken();
};

// 清除用户信息（登出时调用）
const clearUserInfo = () => {
  localStorage.removeItem('token','userName');
  cachedUser = null; // 清除缓存
};
const getUserInfo = () => {
  return localStorage.getItem('userInfo');
};

// 导出这些方法，方便在其他地方使用
export {
  getToken,
  getCurrentUserId,
  getCurrentPhoneNumber,
  getCurrentCreateTime,
  getCurrentUser,
  clearUserInfo ,
  getUserInfo
};
