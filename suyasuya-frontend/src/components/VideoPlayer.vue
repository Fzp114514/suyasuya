<script setup>
import { useDisplayStore } from '@/stores/display'
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { debounce, getBaseUrl, throttle } from '@/main'
import { incrementVideoViews } from '@/api/videoPlayback'
import { usePlaybackStatusStore } from '@/stores/playbackStatus'

const displayStore = useDisplayStore()
const playbackStatusStore = usePlaybackStatusStore()

const isOpenDanmaku = ref()          // 是否开启弹幕
const isControls = ref(false)        // 视频控制器显示状态
const isIndicator = ref(false)       // 进度条标志显示状态
const isVideoPlay = ref(false)       // 视频播放状态
const isVolumeMute = ref(false)      // 视频是否静音
const isFullscreen = ref(false)      // 视频是否全屏
const isInputting = ref(false)       // 是否正在输入文字

// 同步弹幕显示状态至状态管理仓库
const changeDanmakuState = () => {
    displayStore.changeDanmakuDisplayState()
    console.log(displayStore.isOpenDanmaku)
    // 此处可添加用户提示信息
}

const props = defineProps({          // 父组件传值 视频Url
    videoUrl: {
        type: String
    },
    videoId: {                       // 父组件传值 videoId
        type: Number
    }
})

const videoPlayer = ref(null)        // 获取Dom元素 video
const videoContainer = ref(null)     // 获取Dom元素 videoContainer 父组件控制全屏

const currentTime = ref(0)           // 当前视频播放时间
const duration = ref(0)              // 视频总时长
const currentProgress = ref(0)       // 当前进度条百分比
const loadedProgress = ref(0)        // 已加载进度条百分比
const formattedCurrentTime = ref(null)
const formattedDuration = ref(null)


// 初始化操作
const onMetaDataLoaded = () => {
    duration.value = videoPlayer.value.duration
    formattedDuration.value = formatVideoDuration(duration.value)
    formattedCurrentTime.value = formatVideoDuration(currentTime.value)
    const buffered = videoPlayer.value.buffered
    if (buffered.length > 0)
        loadedProgress.value = ((buffered.end(buffered - 1)) / duration.value) * 100
}

// 格式化视频时长
const formatVideoDuration = time => {
    const hours = Math.floor(time / 3600)
    const minutes = Math.floor((time % 3600) / 60)
    const seconds = Math.round(time % 60)
    const formattedHours = String(hours).padStart(2, '0')
    const formattedMinutes = String(minutes).padStart(2, '0')
    const formattedSeconds = String(seconds).padStart(2, '0')
    if (duration.value > 3600)
        return `${formattedHours}:${formattedMinutes}:${formattedSeconds}`
    else if (duration.value > 60)
        return `${formattedMinutes}:${formattedSeconds}`
    else
        return formattedSeconds
}

// 更新当前进度条
const updateCurrentProgress = () => {
    if (!videoPlayer.value) return
    currentTime.value = videoPlayer.value.currentTime
    formattedCurrentTime.value = formatVideoDuration(currentTime.value)
    currentProgress.value = (currentTime.value / duration.value) * 100
}

// 更新缓冲进度条
const updateLoadedProgress = () => {
    const buffered = videoPlayer.value.buffered
    if (buffered.length > 0) {
        for (let i = 0; i < buffered.length; i++) {
            // 寻找当前时间之后最近的点
            if (buffered.start(buffered.length - 1 - i) < currentTime.value) {
                loadedProgress.value = ((buffered.end(buffered.length - 1 - i)) / duration.value) * 100
                break
            }
        }
    }
}

const timeupdate = throttle(updateCurrentProgress, 1000)

// 修改当播放进度方法 (通过点击进度条触发)
const changeCurrentTime = e => {
    const progressBar = e.currentTarget
    const offsetX = e.offsetX
    console.log(e)
    const newTime = (offsetX / progressBar.offsetWidth) * videoPlayer.value.duration
    videoPlayer.value.currentTime = newTime
    updateCurrentProgress()
}

const changePlayingState = () => isVideoPlay.value ? videoPause() : videoPlay()

const videoPlay = () => {
    videoPlayer.value.play()
        .then(() => {
            isVideoPlay.value = true
        })
        .catch(error => {
            console.log('自动播放失败', error)
        })
}
const videoPause = () => {
    videoPlayer.value.pause()
    isVideoPlay.value = false
}

const showControls = () => {
    if (!isFullscreen.value)
        isControls.value = true
}
const hideControls = () => {
    if (!isInputting.value)
        isControls.value = false
}

const showControlsInFullscreen = () => {
    if (isFullscreen.value) {
        isControls.value = true
        if (!isInputting.value) {
            timingHideControls()
        }
    }
}
const timingHideControls = debounce(hideControls, 2000)

const onMouseMove = throttle(showControlsInFullscreen, 100)

// 视频播放音量控制相关方法 传参为0-1区间内的音量改变值
const isVolumePromptMessage = ref(false)

const increaseVideoVolume = value => {
    // 确保音量不超过1
    videoPlayer.value.volume = Math.min(videoPlayer.value.volume + value, 1).toFixed(2);
    playbackStatusStore.setPlaybackVolume(parseInt(videoPlayer.value.volume * 100))
    isVolumePromptMessage.value = true
    timingHideVolumePromptMessage()
    console.log(`当前音量为：${playbackStatusStore.playbackVolume}`)
}

const reduceVideoVolume = value => {
    // 确保音量不小于0
    videoPlayer.value.volume = Math.max(videoPlayer.value.volume - value, 0).toFixed(2);
    playbackStatusStore.setPlaybackVolume(parseInt(videoPlayer.value.volume * 100))
    isVolumePromptMessage.value = true
    timingHideVolumePromptMessage()
    console.log(`当前音量为：${playbackStatusStore.playbackVolume}`)
}

const timingHideVolumePromptMessage = debounce(() => isVolumePromptMessage.value = false, 1000)

// 切换全屏状态方法, 需注意不同浏览器之间的兼容性
const toggleFullscreen = () => {
    if (isFullscreen.value) {
        // 退出全屏
        if (document.exitFullscreen) {
            document.exitFullscreen()
        } else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen()                     // 针对Safari
        } else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen()                      // 针对Firefox
        } else if (document.msExitFullscreen) {
            document.msExitFullscreen()                         // 针对IE
        }
        isFullscreen.value = false

    } else {
        // 进入全屏
        if (videoContainer.value.requestFullscreen) {
            videoContainer.value.requestFullscreen()
        } else if (videoContainer.value.webkitRequestFullscreen) {
            videoContainer.value.webkitRequestFullscreen()      // 针对Safari
        } else if (videoContainer.value.mozRequestFullScreen) {
            videoContainer.value.mozRequestFullScreen()         // 针对Firefox
        } else if (videoContainer.value.msRequestFullscreen) {
            videoContainer.value.msRequestFullscreen()          // 针对IE
        }
        isFullscreen.value = true
    }
}

// 监听页面全屏变化，处理退出全屏时的逻辑(esc退出全屏时执行逻辑)
const handleFullscreenChange = () => {
    if (
        !document.fullscreenElement &&
        !document.webkitFullscreenElement &&
        !document.mozFullScreenElement &&
        !document.msFullscreenElement
    ) {
        // 当不在全屏模式下时
        isFullscreen.value = false
    }
}

// 快捷键触发方法
const handleKeyDown = e => {
    if (isInputting.value)
        return
    const inputKey = e.key
    switch (inputKey) {
        case ' ':
            e.preventDefault()
            isVideoPlay.value ? videoPause() : videoPlay()
            break
        case 'f':
            toggleFullscreen()
            break
        case 'F':
            toggleFullscreen()
            break
        case 'd':
            isOpenDanmaku.value = !isOpenDanmaku.value
            changeDanmakuState()
            break
        case 'D':
            isOpenDanmaku.value = !isOpenDanmaku.value
            changeDanmakuState()
            break
        case 'ArrowLeft':
            videoPlayer.value.currentTime > 5 ?
                videoPlayer.value.currentTime -= 5 : videoPlayer.value.currentTime = 0
            updateCurrentProgress()
            if (!isVideoPlay.value)
                videoPlay()
            break
        case 'ArrowRight':
            videoPlayer.value.currentTime < duration.value - 5 ?
                videoPlayer.value.currentTime += 5 : videoPlayer.value.currentTime = duration.value
            updateCurrentProgress()
            if (!isVideoPlay.value)
                videoPlay()
            break
        case 'ArrowUp':
            e.preventDefault()
            increaseVideoVolume(0.1)
            break
        case 'ArrowDown':
            e.preventDefault()
            reduceVideoVolume(0.1)
            break
        default:
            return
    }
}

const handleInputFocus = e => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === "TEXTAREA")
        isInputting.value = true
}
const handleInputBlur = e => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === "TEXTAREA")
        isInputting.value = false
    if (isFullscreen.value)
        timingHideControls()
}

// 视频自动播放功能
const onVideoLoaded = () => {
    if (videoPlayer.value) {
        videoPlayer.value.volume = (playbackStatusStore.playbackVolume / 100)
        videoPlayer.value.play()
            .then(() => {
                isVideoPlay.value = true
                startTimer()
            })
            .catch(error => {
                console.log('自动播放失败', error)
            })
    }
}

// 开启播放计时方法（用于增加视频播放量）
const time = 30000
const startTimer = () => {
    setTimeout(async () => {
        // 此处调用增加视频播放量方法
        const res = await incrementVideoViews(props.videoId)
        console.log(res)
    }, time)
}

onMounted(async () => {

    isOpenDanmaku.value = displayStore.isOpenDanmaku
    // 添加全屏事件监听
    document.addEventListener('fullscreenchange', handleFullscreenChange)
    document.addEventListener('webkitfullscreenchange', handleFullscreenChange)     // 针对 Safari
    document.addEventListener('mozfullscreenchange', handleFullscreenChange)        // 针对 Firefox
    document.addEventListener('msfullscreenchange', handleFullscreenChange)         // 针对 IE
    // 添加快捷键监听
    document.addEventListener('keydown', handleKeyDown)
    // 添加输入框监听，防止输入时触发快捷键
    document.addEventListener('focus', handleInputFocus, true)
    document.addEventListener('blur', handleInputBlur, true)
})

// 在组件卸载时移除事件监听, 避免内存泄漏
onBeforeUnmount(() => {
    document.removeEventListener('fullscreenchange', handleFullscreenChange)
    document.removeEventListener('webkitfullscreenchange', handleFullscreenChange)  // 针对 Safari
    document.removeEventListener('mozfullscreenchange', handleFullscreenChange)     // 针对 Firefox
    document.removeEventListener('msfullscreenchange', handleFullscreenChange)      // 针对 IE

    document.removeEventListener('keydown', handleKeyDown)

    document.addEventListener('focus', handleInputFocus, true)
    document.addEventListener('blur', handleInputBlur, true)
})
</script>
<template>
    <!-- 视频播放器，可在此扩展视频弹幕相关功能 -->
    <div class="video-player">
        <div ref="videoContainer" class="videoContainer" @mouseenter="showControls" @mouseleave="hideControls"
            @mousemove="onMouseMove">
            <transition name="fade">
                <div v-show="isVolumePromptMessage" class="volume-prompt-message">
                    <span class="volume-prompt-icon btn-volume-prompt-message"></span>
                    <span class="volume-prompt-text">{{ playbackStatusStore.playbackVolume }}%</span>
                </div>
            </transition>
            <video ref="videoPlayer" :src="`${getBaseUrl()}/videos/${videoUrl}`"
                @click="isVideoPlay ? videoPause() : videoPlay()" @loadedmetadata="onMetaDataLoaded"
                @loadeddata="onVideoLoaded" @timeupdate="timeupdate" @progress="updateLoadedProgress"
                @ended="videoPause" @canplay="updateLoadedProgress">
            </video>

            <transition name="fade">
                <div v-show="isControls" class="control-bar-top">
                    <div class="container">
                        <div class="progress-bar-wrap" @mouseup="changeCurrentTime">
                            <div @mouseenter="isIndicator = true" @mouseleave="isIndicator = false"
                                class="progress-bar">
                                <div class="loaded-progress" :style="{ width: loadedProgress + '%' }"></div>
                                <div class="current-progress" :style="{ width: currentProgress + '%' }">
                                    <transition name="indicator">
                                        <div v-show="isIndicator" class="indicator"></div>
                                    </transition>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="control-content">
                        <div class="box-left">
                            <div class="control-btn" @click="changePlayingState">
                                <span v-show="isVideoPlay" class="btn-span btn-pause"></span>
                                <span v-show="!isVideoPlay" class="btn-span btn-play"></span>
                            </div>
                            <div class="time-label">
                                <span class="currentTime">{{ formattedCurrentTime }}</span>
                                <span class="divide"> / </span>
                                <span class="duration">{{ formattedDuration }}</span>
                            </div>
                        </div>
                        <div class="box-center">
                            <div class="danmaku-switch">
                                <el-switch v-model="isOpenDanmaku" @change="changeDanmakuState" inline-prompt
                                    active-text="弹" inactive-text="弹"
                                    style="--el-switch-on-color: #00aeec; --el-switch-off-color: rgb(163, 163, 163)" />
                            </div>
                            <div class="danmaku-setting">
                                <div class="icon"></div>
                            </div>
                            <div class="danmaku-inputbar">
                                <input type="text" placeholder="请开始你的弹幕秀" class="danmaku-input">
                            </div>
                            <div class="send-btn">发送</div>
                        </div>
                        <div class="box-right">
                            <div class="control-btn speed">
                                <span>倍速</span>
                            </div>
                            <div v-show="!isVolumeMute" class="control-btn">
                                <span class="btn-span btn-volume"></span>
                            </div>
                            <div v-show="isVolumeMute" class="control-btn">
                                <span class="btn-span btn-volume-mute"></span>
                            </div>
                            <div class="control-btn">
                                <span class="btn-span btn-setting"></span>
                            </div>
                            <div class="control-btn" @click="toggleFullscreen">
                                <span class="btn-span btn-fullscreen"></span>
                            </div>
                        </div>
                    </div>

                </div>
            </transition>
        </div>
        <div class="control-bar-bottom">
            <div class="player-video-info">在线人数：1</div>
            <div class="danmaku-switch">
                <el-switch v-model="isOpenDanmaku" @change="changeDanmakuState" inline-prompt active-text="弹"
                    inactive-text="弹"
                    style="--el-switch-on-color: #00aeec; --el-switch-off-color: rgb(163, 163, 163)" />
            </div>
            <div class="danmaku-setting">
                <div class="icon"></div>
            </div>
            <div class="danmaku-inputbar">
                <input type="text" placeholder="请开始你的弹幕秀" class="danmaku-input">
            </div>
            <div class="send-btn">发送</div>
        </div>
    </div>
</template>
<style scoped>
@import url(../assets/css/components/videoPlayer.css);
</style>