

// 检查 Token 是否过期
export const checkTokenExpired = () => {
  const token = localStorage.getItem('token');
  const expireTime = localStorage.getItem('token_expire');
  
  if (!token || !expireTime) return false;

  const expireTimestamp = Number(expireTime);
  if (isNaN(expireTimestamp)) return true;

  const isExpired = Date.now() > expireTimestamp;
  if (isExpired) {
    localStorage.removeItem('token');
    localStorage.removeItem('token_expire');
  }
  return isExpired;
};

// 启动 Token 过期监听
let tokenCheckInterval = null;

export const startTokenWatch = (router) => {
  // 先清除旧的定时器（避免重复创建）
  if (tokenCheckInterval) {
    clearInterval(tokenCheckInterval);
    tokenCheckInterval = null;
  }

  // 开发环境：1秒检查一次 / 生产环境：30秒检查一次
  const intervalTime = 1000

  tokenCheckInterval = setInterval(() => {
    if (checkTokenExpired()) {
      clearInterval(tokenCheckInterval);
      router.push('/login');
      // ElMessage.warning('登录已过期，请重新登录');
    }
  }, intervalTime);
};

// 停止监听（在退出登录或组件卸载时调用）
export const stopTokenWatch = () => {
  if (tokenCheckInterval) {
    clearInterval(tokenCheckInterval);
    tokenCheckInterval = null;
  }
};