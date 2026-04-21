import { useSettingStore } from "@/store/settingStore";


/**
 * 是否存在按钮权限
 */
export const hasPermission={
    
    mounted(el,binding){
        //声明菜单仓库
        const settingStore=useSettingStore()
        //判断是否通过指令传值
        if(binding.value){
            //加入当前用户没有任何权限编码
            if(!settingStore.perms||settingStore.perms.length==0){
                el.parentNode?.removeChild(el)
                return
            }
            //
            if(binding.value.indexOf('OR')!=-1){
                const perms=binding.value.split('OR')?.map((value)=>value.trim())
                if(perms.some((value)=>{
                   return settingStore.perms.includes(value)
                })){
                    return
                }
                el.parentNode?.removeChild(el)
                return
            }
            if(binding.value.indexOf('AND')!=-1){
                const perms=binding.value.split('AND')?.map((value)=>value.trim())
                if(perms.every((value)=>{
                   return settingStore.perms.includes(value)
                })){
                    return
                }
                el.parentNode?.removeChild(el)
                return
            }
            if(!settingStore.perms.some((value)=>binding.value===value)){
                el.parentNode?.removeChild(el) 
            }
        }
    }
}