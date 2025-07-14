<script setup>
import { publishVideo, uploadTemporarilyVideo } from '@/api/videoSubmit';
import SecurityRightTitle from '@/components/SecurityRightTitle.vue';
import { useUserStore } from '@/stores/user';
import { ElMessage } from 'element-plus';
import { nextTick, onBeforeUnmount, onMounted, reactive, ref } from 'vue';

const userStore = useUserStore()
// 状态控制
const isUploading = ref(false)          // 是否正在上传视频,暂时设置为true
const isTitleFocus = ref(false)         // 标题输入框是否被选中
const isIntroductionFocus = ref(false)  // 视频简介是否被选中
const uploadProgress = ref(0)           // 视频上传进度

// 要上传的表单数据
const video_id = ref('')                // 视频id
const croppedCoverFile = ref('')        // 裁剪封面blob数据
const video_title = ref('')             // 视频标题
const video_categorieId = ref('')       // 视频分类id
const videoTags = ref([])               // 视频标签
const video_introduction = ref('')      // 视频简介

const submitConfirmDialog = ref(false)  // 视频投稿确认弹窗

// 视频信息校验方法
const videoInfoCheck = () => {
    if (video_id.value === '') {
        ElMessage({
            message: '请等待视频上传成功',
            type: 'error'
        })
        return
    }
    if (croppedCoverFile.value === '') {
        ElMessage({
            message: '请选择视频封面',
            type: 'error'
        })
        return
    }
    if (video_title.value === '') {
        ElMessage({
            message: '请输入视频标题',
            type: 'error'
        })
        return
    }
    if (video_categorie.value === '') {
        ElMessage({
            message: '请选择视频分类',
            type: 'error'
        })
        return
    }
    submitConfirmDialog.value = true
}

// 视频上传方法
const submitVideo = async () => {
    const formData = new FormData()
    formData.append('videoId', video_id.value)
    formData.append('coverFile', croppedCoverFile.value, 'cover.jpeg')
    formData.append('title', video_title.value)
    formData.append('categoryId', video_categorieId.value)
    formData.append('tags', videoTags.value)
    formData.append('introduction', video_introduction.value)
    const res = await publishVideo(formData)
    if (res.success) {
        video_id.value = ''
        croppedCoverFile.value = ''
        video_title.value = ''
        video_categorieId.value = ''
        videoTags.value = []
        video_introduction.value = ''
        uploadProgress.value = 0
        video_categorie.value = ''
        croppedCoverUrl.value = null
        isCoverSelected.value = false
        isUploading.value = false
        ElMessage({
            message: res.message,
            type: 'success'
        })
    }
    else {
        ElMessage({
            message: res.message,
            type: 'error'
        })
        console.log(res)
    }
    submitConfirmDialog.value = false
}


// ================= 其余处理逻辑 ===========================

const videoUrl = ref('')
// 视频上传方法
const uploadVideo = async uploadFile => {
    if (uploadFile) {
        isUploading.value = true
        const file = uploadFile.raw
        // 创建表单对象
        const formData = new FormData()
        formData.append('file', file)
        // 调用接口上传临时视频
        const res = await uploadTemporarilyVideo(formData, percent => {
            uploadProgress.value = percent  //更新上传进度
        })
        console.log(res)
        if (res.success) {
            video_id.value = res.data.videoId
            videoUrl.value = res.data.videoUrl
            ElMessage({
                message: res.message,
                type: 'success'
            })
        }
        else {
            ElMessage({
                message: res.message,
                type: 'error'
            })
        }
    }
}

// 封面上传相关方法

const coverUploader = ref()             // 获取Dom元素 el-upload
const coverUrl = ref('')                // 要上传封面的URL
const isCropDialog = ref(false)         // 裁剪弹窗显示状态
const isCoverSelected = ref(false)      // 是否已经选择视频封面（用于控制封面上传组件是否显示）

const handleExceed = files => {
    coverUploader.value.clearFiles()
    const file = files[0]
    file.uid = genFileId()
    coverUploader.value.handleStart(file)
}
const getCoverUrl = (file, fileList) => {
    if (fileList.length > 0) {
        // 获取图片地址
        coverUrl.value = URL.createObjectURL(file.raw)
        isCropDialog.value = true
    }
}

const cropperRef = ref()            // 获取Dom元素 裁剪组件
const croppedCoverUrl = ref()       // 裁剪图url

// 裁剪组件的相关配置
const option = reactive({
    img: coverUrl,                  // 裁剪图片的地址
    outputSize: 1,                  // 裁剪生成图片的质量(可选0.1 - 1)
    outputType: "jpeg",             // 裁剪生成图片的格式（jpeg || png || webp）
    info: true,                     // 图片大小信息
    canScale: false,                // 图片是否允许滚轮缩放
    autoCrop: true,                 // 是否默认生成截图框
    autoCropWidth: 800,             // 默认生成截图框宽度
    autoCropHeight: 514,            // 默认生成截图框高度
    fixed: false,                    // 是否开启截图框宽高固定比例
    fixedNumber: [280, 180],        // 截图框的宽高比例
    full: true,                     // false按原比例裁切图片，不失真
    fixedBox: false,                // 固定截图框大小，不允许改变
    canMove: false,                 // 上传图片是否可以移动
    canMoveBox: true,               // 截图框能否拖动
    original: false,                // 上传图片按照原始比例渲染
    centerBox: true,                // 截图框是否被限制在图片里面
    high: true,                     // 是否按照设备的dpr 输出等比例图片
    infoTrue: false,                 // true为展示真实输出图片宽高，false展示看到的截图框宽高
    // maxImgSize: 182,             // 限制图片最大宽度和高度
    enlarge: 1,                     // 图片根据截图框输出比例倍数
    // limitMinSize: [280, 180],    // 裁剪框限制最小区域
    mode: 'contain'
})

// 关闭裁剪弹窗触发事件
const closeCropDialog = () => {
    isCropDialog.value ? isCropDialog.value = false : ''
    coverUploader.value.clearFiles()
    coverUrl.value = ''
}
// 保存截图方法
const savaCroppedCover = async () => {
    // 获取裁剪图片文件及其地址
    croppedCoverFile.value = await getCropBlobAsync()
    croppedCoverUrl.value = URL.createObjectURL(croppedCoverFile.value)
    console.log(croppedCoverUrl.value)
    isCoverSelected.value ? '' : isCoverSelected.value = true
    isCropDialog.value = false
}

// 获取截图的 blob 数据
const getCropBlobAsync = () => {
    return new Promise((resolve, reject) => {
        cropperRef.value.getCropBlob((data) => {
            if (data) {
                resolve(data);  // 成功获取到数据，resolve 返回数据
            } else {
                reject(new Error('无法获取裁剪后的图片数据'));  // 如果获取失败，reject
            }
        })
    })
}

// 视频标签相关设置
const tagInputValue = ref('')
const tagInputVisible = ref(false)
const tagInputRef = ref(null)

const handleClose = (tag) => {
    videoTags.value.splice(videoTags.value.indexOf(tag), 1)
}
const showInput = () => {
    tagInputVisible.value = true
    nextTick(() => {
        tagInputRef.value.input.focus()
    })
}
const handleInputConfirm = () => {
    if (tagInputValue.value) {
        if (videoTags.value.includes(tagInputValue.value)) {
            ElMessage({
                message: '该标签已存在',
                type: 'error'
            })
        }
        else videoTags.value.push(tagInputValue.value)
    }
    tagInputVisible.value = false
    tagInputValue.value = ''
}

const video_categorie = ref('')         // 视频类别
const allCategories = ref([             // 所有视频分类（待修改）
    {
        value: '学习',
        label: '学习',
    },
    {
        value: '游戏',
        label: '游戏',
    },
    {
        value: '生活',
        label: '生活',
    },
    {
        value: '杂谈',
        label: '杂谈',
    },
    {
        value: '动漫',
        label: '动漫',
    },
])

// 设置投稿视频的分类id（待修改）
const setVideoCategorieId = () => {
    switch (video_categorie.value) {
        case '学习':
            video_categorieId.value = 1
            break
        case '游戏':
            video_categorieId.value = 2
            break
        case '生活':
            video_categorieId.value = 3
            break
        case '杂谈':
            video_categorieId.value = 4
            break
        case '动漫':
            video_categorieId.value = 5
            break
    }

}


// 其余设置
// 判断是否需要警告
const needWarning = () => {
    return isUploading.value
}

// 浏览器原生事件监听
const handleBeforeUnload = (e) => {
    if (needWarning()) {
        e.preventDefault()
        e.returnValue = '您有未保存的更改，确定离开吗？' // 兼容旧浏览器
    }
}

// 组件挂载时添加监听
onMounted(() => {
    window.addEventListener('beforeunload', handleBeforeUnload);
})
// 组件销毁前移除监听
onBeforeUnmount(() => {
    window.removeEventListener('beforeunload', handleBeforeUnload);
})

</script>
<template>
    <SecurityRightTitle>投稿视频</SecurityRightTitle>

    <div class="container">
        <el-upload v-if="!isUploading" class="upload-demo" :limit="1" :auto-upload="false" :on-change="uploadVideo" drag
            accept="video/*">
            <el-icon class="el-icon--upload"><i-ep-upload-filled /></el-icon>
            <div class="el-upload__text">
                将文件拖拽到此处或 <em>点击上传</em>
            </div>
        </el-upload>
        <div v-else class="video-setting">
            <div>
                <div class="progress-text">视频上传进度</div>
                <el-progress :percentage="uploadProgress"></el-progress>
            </div>
            <div class="video-description">
                <div class="title">基本设置</div>
                <div class="item">
                    <div class="label">封面</div>
                    <div class="cover">
                        <img v-show="isCoverSelected" :src="croppedCoverUrl" class="cover-preview">
                        <el-upload ref="coverUploader" :limit="1" :on-exceed="handleExceed" :on-change="getCoverUrl"
                            :show-file-list="false" :auto-upload="false">
                            <div v-show="!isCoverSelected" class="cover-upload-btn">
                                <el-icon class="cover-upload-btn-icon"><i-ep-Plus /></el-icon>
                            </div>
                            <div v-show="isCoverSelected" class="cover-upload-selected">重新选择</div>
                        </el-upload>

                    </div>
                </div>
                <div class="item">
                    <div class="label">标题</div>
                    <div :class="['item-content', { active: isTitleFocus }]">
                        <input type="text" v-model="video_title" @focus="isTitleFocus = true"
                            @blur="isTitleFocus = false" placeholder="请输入视频标题" maxlength="80">
                        <div class="title-length">{{ video_title.length }}/80</div>
                    </div>
                </div>
                <div class="item">
                    <div class="label">分类</div>
                    <el-select v-model="video_categorie" @change="setVideoCategorieId" placeholder="请选择视频分类"
                        style="width: 240px">
                        <el-option v-for="item in allCategories" :key="item.value" :label="item.label"
                            :value="item.value" />
                    </el-select>
                </div>
                <div class="item">
                    <div class="label">标签</div>
                    <div class="item-content">
                        <div class="video-tag-content">
                            <el-tag v-for="tag in videoTags" :key="tag" closable :disable-transitions="false"
                                class="video-tag" @close="handleClose(tag)">
                                {{ tag }}
                            </el-tag>
                            <el-input v-if="tagInputVisible" ref="tagInputRef" v-model="tagInputValue" class="w-20"
                                size="small" @keyup.enter="handleInputConfirm" @blur="handleInputConfirm" />
                            <el-button v-else class="video-tag" size="small" @click="showInput">
                                + 新增标签
                            </el-button>
                        </div>
                    </div>
                </div>
                <div class="item">
                    <div class="label">简介</div>
                    <div :class="['item-content introduction', { active: isIntroductionFocus }]">
                        <textarea v-model="video_introduction" class="video-introduction" placeholder="请输入视频简介"
                            maxlength="2000" @focus="isIntroductionFocus = true"
                            @blur="isIntroductionFocus = false"></textarea>
                        <div @click="submitConfirmDialog = true" class="introduction-length">
                            {{ video_introduction.length }}/2000
                        </div>
                    </div>
                </div>
            </div>
            <div @click="videoInfoCheck" class="submit-btn">视频投稿</div>
        </div>
    </div>
    <el-dialog v-model="isCropDialog" @close="closeCropDialog" class="crop-dialog">
        <template #header="{ titleId, titleClass }" class="my-header">
            <div>
                <h4 :id="titleId" :class="titleClass">裁剪视频封面</h4>
            </div>
        </template>
        <div class="cropper">
            <VueCropper ref="cropperRef" :img="option.img" :output-size="option.outputSize" :info="option.info"
                :can-scale="option.canScale" :auto-crop="option.autoCrop" :auto-crop-width="option.autoCropWidth"
                :auto-crop-height="option.autoCropHeight" :fixed="option.fixed" :fixed-number="option.fixedNumber"
                :full="option.full" :fixed-box="option.fixedBox" :can-move="option.canMove"
                :can-move-box="option.canMoveBox" :original="option.original" :center-box="option.centerBox"
                :info-true="option.infoTrue" :max-img-size="option.maxImgSize" :enlarge="option.enlarge"
                :limit-min-size="option.limitMinSize" :mode="option.mode">
            </VueCropper>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="closeCropDialog">取消</el-button>
                <el-button type="primary" @click="savaCroppedCover">确定</el-button>
            </div>
        </template>
    </el-dialog>
    <el-dialog v-model="submitConfirmDialog" title="提示" width="500">
        <span>确定要投稿吗</span>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="submitConfirmDialog = false">取消</el-button>
                <el-button type="primary" @click="submitVideo">
                    确认
                </el-button>
            </div>
        </template>
    </el-dialog>
</template>
<style scoped>
@import url(../../assets/css/views/submitVideo.css);
</style>