<script setup lang="ts">
import SecurityRightTitle from '@/components/SecurityRightTitle.vue'
import { updateAvatar } from '@/api/userInfo';
import { ElMessage, genFileId, UploadFile, UploadFiles, UploadInstance } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { computed, reactive, ref } from 'vue'

// 定义实时预览数据类型
interface PreviewData {
    w: number;
    h: number;
    div: {
        width: string;
        height: string;
        overflow: string;
    };
    url: string;
    img: {
        width: string;
        height: string;
        transform: string;
    };
}

const userStore = useUserStore()

// 控制图片是否已选择
const isImageSelected = ref<boolean>(false)

// 要上传图片的URL
const imageUrl = ref(userStore.getUserAvatar())

const upload = ref<UploadInstance>()
const handleExceed = (files: File[]): void => {
    if (!upload.value) return
    upload.value.clearFiles()
    const file: any = files[0]
    file.uid = genFileId()
    upload.value?.handleStart(file as any)
}
const handleChange = (file: UploadFile, fileList: UploadFiles) => {
    if (fileList.length > 0 && file.raw) {
        // 获取图片地址
        imageUrl.value = URL.createObjectURL(file.raw)
        isImageSelected.value = true
        console.log(imageUrl.value)
    }
}


const cropperRef = ref()   // 获取Dom元素 裁剪组件

// 裁剪组件配置
interface CropperOption {
    img: string;
    outputSize: number;
    outputType: 'jpeg' | 'png' | 'webp';
    info: boolean;
    canScale: boolean;
    autoCrop: boolean;
    autoCropWidth: number;
    autoCropHeight: number;
    fixed: boolean;
    fixedNumber: [number, number];
    full: boolean;
    fixedBox: boolean;
    canMove: boolean;
    canMoveBox: boolean;
    original: boolean;
    centerBox: boolean;
    high: boolean;
    infoTrue: boolean;
    enlarge: number;
    limitMinSize: [number, number];
    mode: string;
}

// 裁剪组件的相关配置
const option = reactive<CropperOption>({
    img: imageUrl.value,                  // 裁剪图片的地址
    outputSize: 1,                  // 裁剪生成图片的质量(可选0.1 - 1)
    outputType: "jpeg",             // 裁剪生成图片的格式（jpeg || png || webp）
    info: false,                    // 图片大小信息
    canScale: true,                 // 图片是否允许滚轮缩放
    autoCrop: true,                 // 是否默认生成截图框
    autoCropWidth: 150,             // 默认生成截图框宽度
    autoCropHeight: 150,            // 默认生成截图框高度
    fixed: true,                    // 是否开启截图框宽高固定比例
    fixedNumber: [1, 1],            // 截图框的宽高比例
    full: false,                    // false按原比例裁切图片，不失真
    fixedBox: true,                 // 固定截图框大小，不允许改变
    canMove: true,                  // 上传图片是否可以移动
    canMoveBox: false,              // 截图框能否拖动
    original: false,                // 上传图片按照原始比例渲染
    centerBox: true,                // 截图框是否被限制在图片里面
    high: true,                     // 是否按照设备的dpr 输出等比例图片
    infoTrue: true,                 // true为展示真实输出图片宽高，false展示看到的截图框宽高
    // maxImgSize: 182,             // 限制图片最大宽度和高度
    enlarge: 1,                     // 图片根据截图框输出比例倍数
    limitMinSize: [40, 40],         // 裁剪框限制最小区域
    mode: 'contain'
})

// 实时预览图片
const previews = ref<PreviewData>({} as PreviewData)
const realTime = (data: PreviewData): void => {
    previews.value = data
}
const getPreviewStyle = computed(() => ({

    'width': `${previews.value.w}px`,
    'height': `${previews.value.h}px`,
    'overflow': 'hidden',
}))

// 获取截图的 blob 数据
const getCropBlobAsync = (): Promise<Blob> => {
    return new Promise((resolve, reject) => {
        if (!cropperRef.value) {
            reject(new Error('裁剪组件未挂载'))
            return
        }
        cropperRef.value.getCropBlob((data: Blob) => {
            if (data) {
                resolve(data);  // 成功获取到数据，resolve 返回数据
            } else {
                reject(new Error('无法获取裁剪后的图片数据'));  // 如果获取失败，reject
            }
        })
    })
}

// 头像上传方法 (采用同时上传方法)
const uploadAvatar = async () => {
    const blob = await getCropBlobAsync()
    // 创建表单对象
    const formData = new FormData()
    formData.append('file', blob, 'avatar.jpeg')
    // 此处调用api更新用户头像
    const res = await updateAvatar(formData)
    if (res.success) {
        ElMessage({
            message: res.message,
            type: 'success'
        })
        userStore.updateUserInfo()
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
    }

}

</script>
<template>
    <SecurityRightTitle>我的头像 > 更换头像</SecurityRightTitle>
    <div class="body">
        <div class="avatar-upload-container">

            <div class="left">
                <!-- 显示选择的图片 -->
                <div v-show="isImageSelected" class="image-preview">
                    <VueCropper ref="cropperRef" :img="option.img" :output-size="option.outputSize" :info="option.info"
                        :can-scale="option.canScale" :auto-crop="option.autoCrop"
                        :auto-crop-width="option.autoCropWidth" :auto-crop-height="option.autoCropHeight"
                        :fixed="option.fixed" :fixed-number="option.fixedNumber" :full="option.full"
                        :fixed-box="option.fixedBox" :can-move="option.canMove" :can-move-box="option.canMoveBox"
                        :original="option.original" :center-box="option.centerBox" :info-true="option.infoTrue"
                        :enlarge="option.enlarge" :limit-min-size="option.limitMinSize" :mode="option.mode"
                        @realTime="realTime">
                    </VueCropper>
                </div>
                <el-upload ref="upload" :limit="1" :on-exceed="handleExceed" :on-change="handleChange"
                    :show-file-list="false" :auto-upload="false">
                    <div v-show="!isImageSelected" class="upload-card">
                        本地图片上传
                    </div>
                    <div v-show="isImageSelected" class="upload-card-selected">重新选择</div>
                </el-upload>
            </div>

            <div class="border-line"></div>

            <!-- 实时预览图 -->
            <div class="avatar-preview-wrap">
                <div v-show="!isImageSelected" class="current-avatar">
                    <img :src="`http://localhost:8080/avatar/${encodeURIComponent(imageUrl)}`" alt="">
                </div>

                <div v-show="isImageSelected" class="current-avatar" :style="getPreviewStyle">
                    <div :style="previews.div">
                        <img :src="previews.url" :style="previews.img" alt="">
                    </div>
                </div>

                <div class="avatar-info">{{ isImageSelected ? '预览头像' : '当前头像' }}</div>
            </div>
        </div>
        <div class="descript">请选择图片上传：大小180 * 180像素支持JPG、PNG等格式，图片需小于2M</div>
        <div @click="uploadAvatar" :class="['upload-btn', isImageSelected ? '' : 'disabale']">更新</div>
    </div>
</template>
<style scoped>
.body {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.avatar-upload-container {
    height: 300px;
    display: flex;
    padding: 60px 20px 0;
}

.avatar-upload-container .border-line {
    height: 180px;
    width: 1px;
    background: #e5e9ef;

}

.avatar-upload-container .avatar-preview-wrap {
    margin-top: 30px;
    margin-left: 40px;
}

.avatar-upload-container .left {
    display: flex;
    align-items: center;
    flex-direction: column;
    margin-right: 40px;
}

.avatar-upload-container .left .upload-card {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 178px;
    height: 84px;
    margin-top: 40px;
    background: #f1f2f5;
    border: 1px solid #e5e9ef;
    border-radius: 4px;
    color: #5a6267;
    font-size: 14px;
    transition: all .6s ease;
}

.avatar-upload-container .left .upload-card-selected {
    margin-top: 15px;
    color: #6d757a;
    font-size: 12px;
}

.avatar-upload-container .image-preview {
    margin-top: 20px;
    width: 182px;
    height: 182px;
    border: 1px solid #e5e9ef;
    border-radius: 4px;
}



.avatar-preview-wrap .current-avatar {
    width: 96px;
    height: 96px;
    border-radius: 50%;
    border: 1px solid #e6eaf0;
    overflow: hidden;
}

.avatar-preview-wrap .avatar-info {
    margin-top: 20px;
    font-size: 12px;
    color: #99a2aa;
    text-align: center;
}

.avatar-preview-wrap .current-avatar img {
    width: 100%;
    height: 100%;
    border-radius: 4px;
}

.descript {
    margin-top: 20px;
    color: #99a2aa;
    font-size: 12px;
}

.upload-btn {
    margin-top: 40px;
    width: 140px;
    height: 40px;
    background: #00a1d6;
    color: #fff;
    line-height: 40px;
    text-align: center;
    font-size: 14px;
    border-radius: 4px;
    cursor: pointer;
}

.upload-btn:hover {
    background: #00b5e5;
}

.disabale {
    background: #f4f5f7;
    color: #ccd0d7;
    cursor: default;
}
</style>