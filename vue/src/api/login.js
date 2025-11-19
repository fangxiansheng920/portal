import request from '@/utils/request'

// 账号密码登录
export function authLogin(data) {
  return request({
    url: '/open/loginByPassword',
    method: 'post',
    data: {
      action: 'authLogin',
      ...data
    }
  });
}

//logout
export function logout() {
  return request({
    url: 'open/logout',
    method: 'post',
  });
}
