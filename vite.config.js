import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // Alias for CodeMirror to ensure proper resolution
      'codemirror': path.resolve(__dirname, 'node_modules/codemirror')
    }
  }
});