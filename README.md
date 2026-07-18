# 必应每日壁纸 App

自动获取 Bing(必应)每日壁纸,并设置为**桌面壁纸 + 锁屏壁纸**

## 项目结构

```text
BingW/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/bingwallpaper/
│   │       │   ├── db/                 # Room 数据库相关 (缓存)
│   │       │   ├── utils/              # 工具类 (壁纸设置等)
│   │       │   ├── MainActivity.kt     # 首页 (列表展示)
│   │       │   ├── SettingsActivity.kt # 设置页 (语言、地区、自动更新)
│   │       │   ├── ViewerActivity.kt   # 预览页 (全屏查看、手势切换)
│   │       │   └── ...                 # Adapter、Data 类等
│   │       └── res/                    # 资源文件 (布局、多语言、菜单)
│   └── build.gradle                    # App 级构建配置 (KSP、ABI 限制)
├── .github/workflows/                  # GitHub Actions (自动打包)
├── build.gradle                        # 项目级构建配置
└── README.md
```

## 功能

- 打开 App 点击「立即更新壁纸」可手动触发一次
- 每天零点自动切换daily wallpaper
- 适配Bing的壁纸焦点，竖屏壁纸会自动聚焦画面主体
- 打开「开启每日自动更新」开关后,后台每天自动更新一次(基于 WorkManager,重启手机后任务会自动恢复,无需额外权限)

## HyperOS设置
启用自动切换壁纸时会自动检查电源设置，请设置为无限制！如果不行还要打开自启动。
