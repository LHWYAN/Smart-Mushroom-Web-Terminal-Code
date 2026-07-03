<template>
  <div class="glass-card" :class="{ compact }">
    <div v-if="$slots.header" class="card-header">
      <slot name="header" />
    </div>
    <div class="card-body" :style="bodyStyle">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  compact: { type: Boolean, default: false },
  bodyStyle: { type: Object, default: () => ({}) },
})
</script>

<style scoped>
.glass-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
}
.card-header {
  padding: 14px 18px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-title);
}
.card-body {
  padding: 16px 18px;
}
.compact .card-body {
  padding: 12px 14px;
}
</style>
