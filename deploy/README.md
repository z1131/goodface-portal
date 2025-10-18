# CI/CD 与部署指引（阿里云 ECS -> ACK/K8s）

本项目已集成完整的 CI/CD：
- 使用 GitHub Actions 构建 Maven（Java 11），制作 Docker 镜像并推送到阿里云 ACR。
- 通过 SSH 登录 ECS 主机，拉取最新镜像并用 Docker Compose 方式启动服务。
- 提供 Helm Chart，支持后续迁移到阿里云 ACK/K8s。

## 目录结构
- `src/main/docker/Dockerfile`：应用容器镜像构建文件。
- `deploy/docker-compose.yml`：ECS 上的 Compose 编排文件。
- `.github/workflows/ci-cd.yml`：CI/CD 工作流配置。
- `deploy/helm/goodface-portal/`：Helm Chart 模板。

## GitHub Secrets（必须配置）
为仓库添加以下 Secrets（Settings -> Secrets and variables -> Actions）：

- `ACR_REGISTRY`：你的 ACR 注册域名，例如 `registry.cn-hangzhou.aliyuncs.com`
- `ACR_NAMESPACE`：你的 ACR 命名空间，例如 `my-namespace`
- `ACR_USERNAME`：登录 ACR 的用户名（可用 `aliyun` 账号的 ACR 登录凭据）
- `ACR_PASSWORD`：登录 ACR 的密码/Token

- `ECS_HOST`：ECS 公网 IP 或域名
- `ECS_PORT`：SSH 端口，默认 `22`
- `ECS_USER`：SSH 用户名（如 `root` 或你的普通用户）
- `ECS_SSH_KEY`：SSH 私钥（PEM 格式，复制到 Secret 内容即可）

- `SPRING_DATASOURCE_URL`：数据库 JDBC URL，例如 `jdbc:mysql://your-mysql:3306/goodface?...`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_REDIS_HOST`
- `SPRING_REDIS_PORT`
- `DUBBO_REGISTRY_ADDRESS`：如 `nacos://nacos.your-domain:8848`

> 注意：不要在仓库中保留默认的敏感值。运行时会用 Secrets 覆盖 `application.yml` 中的默认配置。

## ECS 端准备
在 ECS 上安装 Docker 与 Docker Compose（推荐 Docker Compose v2）：

```bash
# 以 root 为例
curl -fsSL https://get.docker.com | bash
systemctl enable docker && systemctl start docker

# Docker Compose v2 (如果未安装)
DOCKER_COMPOSE_VERSION=$(docker compose version || true)
echo "Compose version: $DOCKER_COMPOSE_VERSION" # 确认可用

# 部署目录
sudo mkdir -p /opt/goodface-portal && cd /opt/goodface-portal
```

工作流会将 `deploy/docker-compose.yml` 拷贝到该目录，并执行：
- 登录 ACR
- 导出必要环境变量（Secrets 注入）
- `docker compose pull && docker compose up -d --remove-orphans`

成功后，服务将以容器运行：
- HTTP：`8001`（对外暴露）
- Dubbo：`20880`（仅容器内部网络可访问，不映射到宿主机）
- QOS：`22224`（仅容器内部网络可访问，不映射到宿主机）

## 使用 .env 管理运行时配置（不经 GitHub Secrets）
如果你不想在 GitHub 配置数据库、Redis、Nacos 等运行参数，可在 ECS 上用 `.env` 文件集中管理：

1) 在 `/opt/goodface-portal` 目录创建 `.env` 文件（Compose 会自动识别）：

```
# 镜像信息（由工作流导出或手动指定）
ACR_REGISTRY=registry.cn-hangzhou.aliyuncs.com
ACR_NAMESPACE=goodface
IMAGE_TAG=<由工作流传入或你手动指定>

# 应用运行配置
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://rm-xxx.mysql.rds.aliyuncs.com:3306/goodface?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=goodface
SPRING_DATASOURCE_PASSWORD=strong-password
SPRING_REDIS_HOST=r-xxx.redis.rds.aliyuncs.com
SPRING_REDIS_PORT=6379
DUBBO_REGISTRY_ADDRESS=nacos://nacos.internal:8848
```

2) 工作流的部署步骤已改为：

```
docker compose --env-file .env pull
docker compose --env-file .env up -d --remove-orphans
```

3) 注意变量优先级：Shell 导出的环境变量会覆盖 `.env`。当前我们不再从工作流导出运行时配置，确保 `.env` 生效。

## 触发与回滚
- 推送到 `main` 分支会触发 CI/CD，镜像 tag 使用 `github.sha`。
- 回滚：在 ECS 上指定旧的 `IMAGE_TAG` 后重新执行 `docker compose up -d`。

## Helm（ACK/K8s）部署
1. 将镜像地址写到 `deploy/helm/goodface-portal/values.yaml`：
   ```yaml
   image:
     repository: "<your-acr-registry>/<your-namespace>/goodface-portal"
     tag: "latest"
   ```

2. 在 K8s 集群执行：
   ```bash
   helm upgrade --install goodface-portal deploy/helm/goodface-portal
   ```