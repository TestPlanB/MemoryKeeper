# MemoryKeeper<br>
## 用于内存泄漏兜底工作，目前已支持activity

* **已支持组件：ImageView TextView ListView LinearLayout等多个常用组件进行内存兜底工作，释放产生泄漏的内存**
* **释放时机：采用检测协程，检测10秒后进行释放，避免由于gc的原因进行不必要的释放操作**
