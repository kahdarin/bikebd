import axios,{InternalAxiosRequestConfig,AxiosResponse} from 'axios'
import { ElLoading } from 'element-plus'
import { ElMessage } from 'element-plus'

let loading:any;
const startLoading = () =>{
    interface Options{
        lock: boolean;
        text: string;
        background: string;
    }
    const options:Options = {
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.7)'
    }
    loading = ElLoading.service(options)
}
const endLoading = ()=>{
    loading.close()
}


// 请求拦截
axios.interceptors.request.use((config:InternalAxiosRequestConfig<any>)=>{
    // 开始Loading
    startLoading()
    return config
})

//请求响应拦截
axios.interceptors.response.use((res:AxiosResponse<any, any>)=>{
    endLoading()
    // 成功直接返回响应数据
    console.log(res.status)
    //console.log(res.data)
    if(res.status === 200){
        return res.data
    }
},error=>{
    endLoading()
    const { response: res } = error
    const msg = typeof res.data === 'string' ? res.data: res.data.error || '请求错误，请稍后重试'
    ElMessage.error(msg)
    // 错误提醒
    return Promise.reject(error)
})

export default axios
