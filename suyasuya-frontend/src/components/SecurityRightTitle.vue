<script setup lang="ts"></script>
<template>
    <div class="title">
        <div class="text">
            <slot></slot>
        </div>
    </div>
</template>
<style scoped>
.title {
    display: flex;
    align-items: center;
    height: 50px;
    width: 100%;
    padding-left: 30px;
    border-bottom: 1px solid #e1e2e5;

}

.title .text {
    height: 16px;
    padding-left: 5px;
    border-left: 4px solid #00a1d6;
    color: #00a1d6;
    font-size: 14px;
    line-height: 16px;
}
</style>