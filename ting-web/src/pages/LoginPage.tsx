import { useState } from 'react'
import { Button, Card, Form, Input, Typography, theme } from 'antd'
import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

interface LoginForm {
  username: string
  password: string
}

export default function LoginPage() {
  const { token, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [loading, setLoading] = useState(false)
  const { token: themeToken } = theme.useToken()

  if (token) {
    return <Navigate to="/products" replace />
  }

  const onFinish = async (values: LoginForm) => {
    setLoading(true)
    try {
      await login(values)
      const redirect =
        (location.state as { from?: string } | null)?.from ||
        new URLSearchParams(location.search).get('redirect') ||
        '/products'
      navigate(redirect, { replace: true })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'grid',
        placeItems: 'center',
        background: `linear-gradient(145deg, ${themeToken.colorPrimaryBg} 0%, #f5f5f5 45%, #eef2ff 100%)`,
        padding: 24,
      }}
    >
      <Card style={{ width: 380, boxShadow: themeToken.boxShadowSecondary }}>
        <Typography.Title level={3} style={{ textAlign: 'center', marginBottom: 8 }}>
          Ting-Full
        </Typography.Title>
        <Typography.Paragraph type="secondary" style={{ textAlign: 'center' }}>
          React + Ant Design · 对接网关鉴权
        </Typography.Paragraph>
        <Form<LoginForm>
          size="large"
          initialValues={{ username: 'admin', password: '123456' }}
          onFinish={onFinish}
        >
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" autoComplete="username" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              autoComplete="current-password"
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}>
              登录
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}
