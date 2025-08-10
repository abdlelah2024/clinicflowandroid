# تطبيق ClinicFlow للأندرويد

تطبيق أندرويد أصلي لإدارة العيادات مع ميزات البحث السريع والإضافة التلقائية للمرضى والمواعيد.

## الميزات الرئيسية

### 🔍 البحث السريع المتقدم
- البحث بالاسم أو رقم الهاتف
- نتائج فورية أثناء الكتابة
- إضافة مريض جديد تلقائياً إذا لم يتم العثور على النتائج
- إضافة موعد جديد تلقائياً للمرضى الموجودين

### 👥 إدارة المرضى
- إضافة مرضى جدد بسهولة
- تعبئة البيانات التلقائية من البحث
- حفظ معلومات شاملة للمرضى
- دعم جهات الاتصال في حالات الطوارئ

### 📅 إدارة المواعيد
- جدولة مواعيد سريعة
- اختيار الأوقات المتاحة بصرياً
- أنواع مواعيد متعددة (استشارة، متابعة، طوارئ، إلخ)
- فحص توفر الأوقات تلقائياً

### 🔐 المصادقة والأمان
- تسجيل دخول آمن باستخدام Firebase Authentication
- حساب تجريبي للاختبار
- حماية البيانات الشخصية

### 📱 واجهة مستخدم متجاوبة
- تصميم Material Design
- دعم اللغة العربية
- واجهة سهلة الاستخدام
- تفاعل باللمس السلس

### 🌐 العمل دون اتصال
- دعم العمل دون اتصال بالإنترنت
- مزامنة البيانات عند توفر الاتصال
- تخزين محلي للبيانات المهمة

## المتطلبات التقنية

- **Android SDK**: 24 أو أحدث (Android 7.0+)
- **Kotlin**: 1.9.10
- **Firebase**: Authentication, Firestore, Storage
- **Material Design**: 1.10.0
- **Gradle**: 8.1.2

## إعداد المشروع

### 1. متطلبات التطوير
```bash
- Android Studio Arctic Fox أو أحدث
- JDK 8 أو أحدث
- Android SDK 34
```

### 2. إعداد Firebase
1. إنشاء مشروع Firebase جديد أو استخدام المشروع الحالي
2. إضافة تطبيق Android إلى المشروع
3. تنزيل ملف `google-services.json` ووضعه في مجلد `app/`
4. تفعيل خدمات Firebase المطلوبة:
   - Authentication (Email/Password)
   - Firestore Database
   - Storage

### 3. بناء التطبيق
```bash
# استنساخ المشروع
git clone [repository-url]
cd ClinicFlowAndroid

# بناء التطبيق
./gradlew build

# تشغيل التطبيق
./gradlew installDebug
```

## هيكل المشروع

```
app/
├── src/main/
│   ├── java/com/clinicflow/android/
│   │   ├── adapters/          # محولات RecyclerView
│   │   ├── dialogs/           # حوارات الإضافة السريعة
│   │   ├── models/            # نماذج البيانات
│   │   ├── services/          # خدمات Firebase
│   │   ├── MainActivity.kt    # الشاشة الرئيسية
│   │   └── LoginActivity.kt   # شاشة تسجيل الدخول
│   ├── res/
│   │   ├── layout/            # تخطيطات XML
│   │   ├── drawable/          # الأيقونات والرسوم
│   │   ├── values/            # الألوان والنصوص والأنماط
│   │   └── ...
│   └── AndroidManifest.xml
├── google-services.json       # إعدادات Firebase
└── build.gradle              # إعدادات البناء
```

## الاستخدام

### تسجيل الدخول
1. افتح التطبيق
2. أدخل بيانات تسجيل الدخول أو استخدم "دخول تجريبي"
3. انقر على "تسجيل الدخول"

### البحث السريع
1. في الشاشة الرئيسية، اكتب في حقل "البحث السريع"
2. ستظهر النتائج تلقائياً أثناء الكتابة
3. انقر على نتيجة للوصول إلى خيارات سريعة

### إضافة مريض جديد
1. ابحث عن المريض أولاً
2. إذا لم يتم العثور على نتائج، انقر "إضافة مريض جديد"
3. املأ البيانات المطلوبة
4. انقر "حفظ"

### إضافة موعد جديد
1. ابحث عن المريض أو انقر "إضافة موعد جديد"
2. اختر التاريخ من التقويم
3. اختر الوقت المتاح من الشبكة
4. حدد نوع الموعد
5. أضف ملاحظات إضافية (اختياري)
6. انقر "حفظ"

## إعدادات Firebase

### Firestore Collections
```
patients/
├── {patientId}/
│   ├── name: string
│   ├── phone: string
│   ├── email: string
│   ├── dateOfBirth: timestamp
│   ├── gender: string
│   ├── address: string
│   ├── medicalHistory: string
│   ├── allergies: string
│   ├── emergencyContact: string
│   ├── emergencyContactPhone: string
│   ├── createdAt: timestamp
│   ├── updatedAt: timestamp
│   └── isActive: boolean

appointments/
├── {appointmentId}/
│   ├── patientId: string
│   ├── patientName: string
│   ├── patientPhone: string
│   ├── doctorId: string
│   ├── doctorName: string
│   ├── appointmentDate: timestamp
│   ├── appointmentTime: string
│   ├── duration: number
│   ├── type: string
│   ├── status: string
│   ├── notes: string
│   ├── symptoms: string
│   ├── diagnosis: string
│   ├── treatment: string
│   ├── prescription: string
│   ├── followUpDate: timestamp
│   ├── createdAt: timestamp
│   ├── updatedAt: timestamp
│   └── isActive: boolean
```

### Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their data
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## الميزات المتقدمة

### العمل دون اتصال
- يدعم التطبيق العمل دون اتصال بالإنترنت
- البيانات تُحفظ محلياً وتُزامن عند توفر الاتصال
- إشعارات حالة الاتصال

### البحث الذكي
- البحث في أسماء المرضى وأرقام الهواتف
- نتائج فورية أثناء الكتابة
- ترتيب النتائج حسب الصلة

### إدارة الأوقات
- فحص توفر الأوقات تلقائياً
- منع تداخل المواعيد
- عرض بصري للأوقات المتاحة وغير المتاحة

## استكشاف الأخطاء

### مشاكل شائعة

#### خطأ في الاتصال بـ Firebase
```
تأكد من:
- صحة ملف google-services.json
- تفعيل خدمات Firebase المطلوبة
- صحة قواعد الأمان في Firestore
```

#### مشاكل في تسجيل الدخول
```
تأكد من:
- تفعيل Email/Password في Firebase Authentication
- إنشاء مستخدم تجريبي للاختبار
- صحة بيانات تسجيل الدخول
```

#### مشاكل في البحث
```
تأكد من:
- وجود فهارس مناسبة في Firestore
- صحة قواعد الأمان للقراءة
- اتصال الإنترنت للبحث الأولي
```

## المساهمة

نرحب بالمساهمات! يرجى اتباع الخطوات التالية:

1. Fork المشروع
2. إنشاء branch جديد للميزة
3. Commit التغييرات
4. Push إلى Branch
5. إنشاء Pull Request

## الترخيص

هذا المشروع مرخص تحت رخصة MIT. راجع ملف LICENSE للتفاصيل.

## الدعم

للحصول على الدعم أو الإبلاغ عن مشاكل:
- إنشاء Issue في GitHub
- التواصل عبر البريد الإلكتروني
- مراجعة الوثائق

---

© 2024 ClinicFlow. جميع الحقوق محفوظة.

