# Deployment

## Frontend: GitHub Pages

This repository deploys the Vite/Vue frontend from `frontend/` using `.github/workflows/deploy-frontend-pages.yml`.

The expected GitHub Pages URL for this repository is:

```text
https://liquid9001.github.io/codegeneration/
```

Required GitHub settings:

1. Go to `Settings` > `Pages`.
2. Set `Build and deployment` > `Source` to `GitHub Actions`.
3. Go to `Settings` > `Secrets and variables` > `Actions` > `Variables`.
4. Add `VITE_API_URL` with your Render backend URL, for example:

```text
https://your-render-service.onrender.com
```

The workflow runs on pushes to `main` or `master` when `frontend/**` or the workflow file changes. It can also be started manually from the `Actions` tab.

If you deploy this repository under a different GitHub Pages path, change `VITE_BASE_PATH` in `.github/workflows/deploy-frontend-pages.yml`.

## Backend: Render

Set this Render environment variable so the Spring backend accepts browser requests from GitHub Pages:

```text
CORS_ALLOWED_ORIGINS=https://liquid9001.github.io
```

For local development, no Render variable is needed. The backend defaults CORS to `http://localhost:5173`, and the frontend defaults to `http://localhost:8080`.
