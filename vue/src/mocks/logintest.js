// mockLogin示例（严格模仿后端格式）
export const mockLogin = async (params) => {
  // 模拟登录成功（状态码3）
  if (params.phoneNumber === '123' && params.password === '123') {
    return {
      "code": 200,
      "status": true,
      "message": "登录成功",
      "data": {
        "status": 3,
        "token": "MOCK_TOKEN_123456",
        "userName": "测试用户"
      }
    };
  }
  // 模拟账号不存在（状态码0）
  else if (params.phoneNumber === '123') {
    return {
      "code": 200,  // 接口请求成功，但业务失败
      "status": true,
      "message": "登录成功",  // 这里后端可能返回空，以data.status为准
      "data": {
        "status": 0,
        "token": "",
        "userName": ""
      }
    };
  }
  // 模拟服务器错误（code=500）
  else {
    return {
      "code": 500,
      "status": false,
      "message": "服务器内部错误",
      "data": {}  // 失败时data为空
    };
  }
};