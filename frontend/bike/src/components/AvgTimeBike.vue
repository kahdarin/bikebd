<template>
  <div style="display: flex; justify-content: center;">
    <h2>平均骑行时间</h2>
  </div>
  
  <div style="display: flex; justify-content: center;">
    
    <div ref="chart" style="width: 600px; height: 400px;"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import axios from 'axios';

export default {
  data() {
    return {
      durationData: [],
      chartInstance: null
    }
  },
  mounted() {
    this.initChart();
    this.fetchData();
  },
  methods: {
    fetchData() {
      axios.post('/data', { task: "DataDayAvg" })
        .then(response => {
          console.log('data Response time Record:', response.msg);
          if (response.type === 'Ok') {
            this.durationData = response.msg
            console.log("durationData:", this.durationData)
            this.timeFormat();
            this.updateChart();
            console.log("durationData:", this.durationData)
          } else {
            this.$message.error("没有符合要求的记录!")
          }
        })
        .catch(error => {
          console.error('获取记录信息失败', error);
        });
    },
    initChart() {
      this.chartInstance = echarts.init(this.$refs.chart);
      const option = {
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value',
          data: []
        },
        series: [{
          data: [],
          type: 'line'
        }]
      };
      this.chartInstance.setOption(option);
    },
    timeFormat() {
      this.durationData = this.durationData.map(item => ({
        ...item,
        avg_duration: item.avg_duration.split(':').reduce((acc, time) => (60 * acc) + +time) / 60
      }));
    },
    updateChart(){
      const option = {
        xAxis: {
          data: this.durationData.map(item => item.date)
        },
        series: [{
          data: this.durationData.map(item => item.avg_duration)
        }]
      };
      this.chartInstance.setOption(option);
    }
  }
}

</script>