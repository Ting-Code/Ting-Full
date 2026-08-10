import { useEffect, useState } from 'react'
import {
  Space,
  Table,
  Tag,
  Typography,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  message,
} from 'antd'
import type { ColumnsType } from 'antd/es/table'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons'
import { createProduct, deleteProduct, fetchProducts, type Product, type ProductSavePayload } from '../api/product'
import { useAuth } from '../auth/AuthContext'

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
  const { isAdmin } = useAuth()
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState<Product[]>([])
  const [open, setOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [form] = Form.useForm<ProductSavePayload>()

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

  const onCreate = async () => {
    const values = await form.validateFields()
    setSubmitting(true)
    try {
      await createProduct({ ...values, status: 1 })
      message.success('已创建')
      setOpen(false)
      form.resetFields()
      await load()
    } finally {
      setSubmitting(false)
    }
  }

  const onDelete = async (id: number) => {
    await deleteProduct(id)
    message.success('已删除')
    await load()
  }

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

  if (isAdmin) {
    columns.push({
      title: '操作',
      width: 100,
      render: (_, row) => (
        <Popconfirm title="确认删除？" onConfirm={() => void onDelete(row.id)}>
          <Button type="link" danger>
            删除
          </Button>
        </Popconfirm>
      ),
    })
  }

  return (
    <Space orientation="vertical" size="large" style={{ width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Typography.Title level={4} style={{ margin: 0 }}>
            商品管理
          </Typography.Title>
          <Typography.Text type="secondary">
            登录用户可查看；新增/删除需 ADMIN（网关 RBAC）
          </Typography.Text>
        </div>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => void load()} loading={loading}>
            刷新
          </Button>
          {isAdmin && (
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setOpen(true)}>
              新增商品
            </Button>
          )}
        </Space>
      </div>
      <Table rowKey="id" loading={loading} columns={columns} dataSource={data} pagination={false} />

      <Modal
        title="新增商品"
        open={open}
        onCancel={() => setOpen(false)}
        onOk={() => void onCreate()}
        confirmLoading={submitting}
        destroyOnHidden
      >
        <Form form={form} layout="vertical" initialValues={{ price: 9.9, stock: 10 }}>
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input placeholder="商品名称" />
          </Form.Item>
          <Form.Item name="price" label="价格" rules={[{ required: true, message: '请输入价格' }]}>
            <InputNumber min={0} precision={2} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="stock" label="库存" rules={[{ required: true, message: '请输入库存' }]}>
            <InputNumber min={0} precision={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  )
}
