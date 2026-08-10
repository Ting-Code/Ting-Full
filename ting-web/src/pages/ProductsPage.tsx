import { useEffect, useState } from 'react'
import { Space, Table, Tag, Typography, Button } from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { ReloadOutlined } from '@ant-design/icons'
import { fetchProducts, type Product } from '../api/product'

function formatTime(value?: string | number[]) {
  if (!value) return '-'
  if (typeof value === 'string') return value.replace('T', ' ')
  if (Array.isArray(value) && value.length >= 6) {
    const [y, m, d, h, mi, s] = value
    const pad = (n: number) => String(n).padStart(2, '0')
    return `${y}-${pad(m)}-${pad(d)} ${pad(h)}:${pad(mi)}:${pad(s)}`
  }
  return String(value)
}

export default function ProductsPage() {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<Product[]>([])

  const load = async () => {
    setLoading(true)
    try {
      const list = await fetchProducts()
      setData(list)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void load()
  }, [])

  const columns: ColumnsType<Product> = [
    { title: 'ID', dataIndex: 'id', width: 80 },
    { title: '名称', dataIndex: 'name' },
    {
      title: '价格',
      dataIndex: 'price',
      render: (v: number) => `¥ ${Number(v).toFixed(2)}`,
    },
    { title: '库存', dataIndex: 'stock', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: number) =>
        status === 1 ? <Tag color="success">上架</Tag> : <Tag>下架</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      render: formatTime,
    },
  ]

  return (
    <Space orientation="vertical" size="large" style={{ width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Typography.Title level={4} style={{ margin: 0 }}>
            商品管理
          </Typography.Title>
          <Typography.Text type="secondary">数据来自 ting-biz，经网关 Token 鉴权</Typography.Text>
        </div>
        <Button icon={<ReloadOutlined />} onClick={() => void load()} loading={loading}>
          刷新
        </Button>
      </div>
      <Table rowKey="id" loading={loading} columns={columns} dataSource={data} pagination={false} />
    </Space>
  )
}
