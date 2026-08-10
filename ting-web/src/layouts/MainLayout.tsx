import { Layout, Menu, Dropdown, theme, Typography, Space } from 'antd'
import {
  AppstoreOutlined,
  LogoutOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const { Header, Sider, Content } = Layout

export default function MainLayout() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider breakpoint="lg" collapsedWidth={64}>
        <div className="logo">Ting-Full</div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={[
            {
              key: '/products',
              icon: <AppstoreOutlined />,
              label: <Link to="/products">商品管理</Link>,
            },
          ]}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: colorBgContainer,
            display: 'flex',
            justifyContent: 'flex-end',
            paddingInline: 24,
          }}
        >
          <Dropdown
            menu={{
              items: [
                {
                  key: 'logout',
                  icon: <LogoutOutlined />,
                  label: '退出登录',
                  onClick: () => {
                    logout()
                    navigate('/login', { replace: true })
                  },
                },
              ],
            }}
          >
            <Space style={{ cursor: 'pointer' }}>
              <UserOutlined />
              <Typography.Text>{user?.nickname || user?.username || '用户'}</Typography.Text>
            </Space>
          </Dropdown>
        </Header>
        <Content style={{ margin: 24 }}>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}
