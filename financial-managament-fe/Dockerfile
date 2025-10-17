# # Gunakan image Nginx ringan
# FROM nginx:alpine

# # Hapus default html nginx
# RUN rm -rf /usr/share/nginx/html/*

# # Salin hasil build dari host ke image
# COPY dist/ /usr/share/nginx/html

# # Salin konfigurasi custom nginx
# COPY nginx.conf /etc/nginx/nginx.conf

# # Expose port nginx
# EXPOSE 80

# # Jalankan nginx
# CMD ["nginx", "-g", "daemon off;"]



FROM node:20

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 5173

CMD ["npm", "run", "dev"]