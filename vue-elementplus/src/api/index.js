import instance from "@/utils/Request"
/**
 * 菜单管理列表接口API
 */
export const useGetNavMenusApi=(param)=>{
    return instance.post('/menu/menus',param)
}
/**
 * 用户权限编码集合接口API
 */
export const useGetPermsApi=()=>{
    return instance.get('/permission/perms')
}

/**
 * 上传接口API
 * @param file 
 * @returns 
 */
export const useUploadAvatarImageApi = (data) => {
    return instance.post("/upload/avatar", 
        data
    , {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}