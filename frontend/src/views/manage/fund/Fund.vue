<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
        <!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
<!--        <a-button @click="batchDelete">删除</a-button>-->
        <div style="font-size: 13px;font-family: SimHei">
          基金余额：{{ max }}元
        </div>
      </div>
      <a-tabs default-active-key="1" @change="callback">
        <a-tab-pane key="1" tab="基金收入">
          <!-- 表格区域 -->
          <a-table ref="TableInfo"
                   :columns="columns"
                   :rowKey="record => record.id"
                   :dataSource="dataSource"
                   :pagination="pagination"
                   :loading="loading"
                   :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
                   :scroll="{ x: 900 }"
                   @change="handleTableChange">
            <template slot="operation" slot-scope="text, record">
              <a-icon type="pushpin" @click="handleClose(record)" title="删 除" v-if="record.status != 3 && record.status != 5"></a-icon>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="2" tab="基金支出">
          <!-- 表格区域 -->
          <a-table ref="TableInfo"
                   :columns="postColumns"
                   :rowKey="record => record.id"
                   :dataSource="dataSource"
                   :pagination="pagination"
                   :loading="loading"
                   :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
                   :scroll="{ x: 900 }"
                   @change="handleTableChange">
            <template slot="operation" slot-scope="text, record">
              <a-icon type="pushpin" @click="handleClose(record)" title="删 除" v-if="record.status != 3 && record.status != 5"></a-icon>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </div>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'User',
  components: {RangeDate},
  data () {
    return {
      userView: {
        visiable: false,
        data: null
      },
      advanced: false,
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: [],
      max: 0,
      currentKey: 1
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '所属房屋',
        dataIndex: 'address',
        scopedSlots: {customRender: 'addressShow'}
      }, {
        title: '业主姓名',
        dataIndex: 'name'
      }, {
        title: '联系方式',
        dataIndex: 'phone'
      }, {
        title: '缴费金额',
        dataIndex: 'price',
        customRender: (text, row, index) => {
          if (text !== null) {
            return '￥' + text
          } else {
            return '- -'
          }
        }
      }, {
        title: '缴费时间',
        dataIndex: 'createDate'
      }, {
        title: '费用时间',
        dataIndex: 'year',
        customRender: (text, row, index) => {
          return row.year + '年' + row.month + '月'
        }
      }]
    },
    postColumns () {
      return [ {
        title: '维修编号',
        dataIndex: 'code'
      }, {
        title: '维修金额',
        dataIndex: 'price'
      }, {
        title: '维修内容',
        dataIndex: 'content'
      }, {
        title: '维修前余额',
        dataIndex: 'beforePrice'
      }, {
        title: '维修后余额',
        dataIndex: 'afterPrice'
      }]
    }
  },
  mounted () {
    this.fetch({type: 1})
    this.getRepairFund()
  },
  methods: {
    getRepairFund () {
      this.$get('/cos/repair-record-info/fund').then((r) => {
        this.max = r.data.data.price
      })
    },
    handleClose (row) {
      row.status = 5
      this.$post(`/cos/interview-info/audit`, {...row}).then((r) => {
        this.$message.success('撤销成功')
        this.search()
      })
    },
    view (row) {
      this.userView.data = row
      this.userView.visiable = true
    },
    handleUserViewClose () {
      this.userView.visiable = false
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/cos/interview-info/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        type: this.currentKey,
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    callback (key) {
      this.currentKey = key
      this.fetch({type: key})
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }

      this.$get(params.type.toString() === '1' ? '/cos/repair-record-info/fund/page' : '/cos/repair-record-info/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>
@import "../../../../static/less/Common";
</style>
