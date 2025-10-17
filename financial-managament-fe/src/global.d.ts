
declare global {
  interface Window {
    RUNTIME_CONFIG?: {
      VITE_REST_API_BASE_URL: string;
      VITE_REST_API_BASE_URL_AUTH: string;
      VITE_REST_API_BASE_URL_PUBLIC: string;
    };
  }
}

export {};
