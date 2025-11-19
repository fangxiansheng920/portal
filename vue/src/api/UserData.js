import request from '@/utils/request';


//获得用户信息
export const getCurrentUserInfo = async () => {
  const res = await request.get('/portal/getUserInfo');
  
  // 关键修改：检查返回格式是否符合预期
  if (res && res.code === 200 && res.data) {
    return res; // 返回完整响应（包含 res.data）
  } else {
    console.error('用户信息接口返回异常:', res);
    throw new Error('获取用户信息失败');
  }
};

//修改用户信息（头像+姓名）
export const modifyUserInfo = async(userName,avatar) =>{
  const res = await request.post('/portal/modifyUserInfo',{
    userName:userName,
    avatar:avatar
  });
  return res;
}

//设置密码
export const setPassword = async(parmas)=>{
  return request.post('/portal/setPassword', parmas);
}


//修改密码
export const changePassword = async(parmas)=>{
  return request.post('/portal/changePassword', parmas);
}