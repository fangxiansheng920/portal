const path = require('path');

module.exports = {
  parser: '@babel/eslint-parser',
  parserOptions: {
    babelOptions: {
      configFile: path.resolve(__dirname, 'babel.config.js') // 使用绝对路径
    }
  },
  rules: {
    'parser': 0 // 临时禁用解析器错误检查
  }
};