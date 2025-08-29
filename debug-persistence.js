// 数据持久化调试脚本
// 在浏览器Console中运行此脚本来诊断问题

console.log('=== 数据持久化调试脚本 ===');

// 1. 检查localStorage状态
function checkLocalStorage() {
    console.log('\n--- 1. 检查localStorage状态 ---');
    
    const keys = Object.keys(localStorage);
    console.log('localStorage键列表:', keys);
    
    if (keys.length === 0) {
        console.log('❌ localStorage为空');
        return false;
    }
    
    keys.forEach(key => {
        try {
            const value = localStorage.getItem(key);
            console.log(`${key}:`, value);
            
            if (key === 'topics' || key === 'currentUser') {
                const parsed = JSON.parse(value);
                console.log(`${key} (解析后):`, parsed);
            }
        } catch (error) {
            console.error(`解析${key}失败:`, error);
        }
    });
    
    return true;
}

// 2. 检查Vue应用状态
function checkVueAppState() {
    console.log('\n--- 2. 检查Vue应用状态 ---');
    
    // 尝试获取Vue应用实例
    const app = document.querySelector('#app');
    if (!app) {
        console.log('❌ 未找到Vue应用容器');
        return false;
    }
    
    console.log('Vue应用容器:', app);
    
    // 检查是否有用户登录
    const userInfo = document.querySelector('.nav-user span');
    if (userInfo) {
        console.log('当前用户:', userInfo.textContent);
    } else {
        console.log('❌ 未找到用户信息，可能未登录');
    }
    
    // 检查是否有专题数据
    const topicElements = document.querySelectorAll('.topic-item');
    console.log('专题数量:', topicElements.length);
    
    return true;
}

// 3. 模拟数据操作
function simulateDataOperations() {
    console.log('\n--- 3. 模拟数据操作 ---');
    
    // 检查是否有用户登录
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
        console.log('❌ 没有用户登录，无法进行数据操作');
        return false;
    }
    
    try {
        const user = JSON.parse(currentUser);
        console.log('当前用户:', user.username);
        
        // 获取用户数据
        const allTopics = JSON.parse(localStorage.getItem('topics') || '{}');
        const userTopics = allTopics[user.username] || [];
        
        console.log('用户专题数量:', userTopics.length);
        
        // 添加测试数据
        const testTopic = {
            id: Date.now().toString(),
            name: '调试测试专题',
            description: '这是一个用于调试的测试专题',
            createdAt: new Date().toISOString(),
            tasks: []
        };
        
        userTopics.push(testTopic);
        allTopics[user.username] = userTopics;
        
        // 保存数据
        localStorage.setItem('topics', JSON.stringify(allTopics));
        console.log('✅ 测试数据已添加并保存');
        
        // 验证保存
        const savedData = JSON.parse(localStorage.getItem('topics'));
        const savedUserTopics = savedData[user.username] || [];
        console.log('保存后的专题数量:', savedUserTopics.length);
        
        return true;
    } catch (error) {
        console.error('数据操作失败:', error);
        return false;
    }
}

// 4. 检查数据变化监听
function checkDataWatchers() {
    console.log('\n--- 4. 检查数据变化监听 ---');
    
    // 检查Vue的watch是否正常工作
    const topics = localStorage.getItem('topics');
    if (topics) {
        try {
            const parsed = JSON.parse(topics);
            console.log('当前topics数据:', parsed);
            
            // 模拟数据变化
            const keys = Object.keys(parsed);
            if (keys.length > 0) {
                const firstUser = keys[0];
                const userTopics = parsed[firstUser] || [];
                
                if (userTopics.length > 0) {
                    // 修改第一个专题的名称
                    const originalName = userTopics[0].name;
                    userTopics[0].name = originalName + ' (已修改)';
                    
                    // 保存修改
                    parsed[firstUser] = userTopics;
                    localStorage.setItem('topics', JSON.stringify(parsed));
                    
                    console.log('✅ 数据修改测试完成');
                    console.log('修改前名称:', originalName);
                    console.log('修改后名称:', userTopics[0].name);
                }
            }
        } catch (error) {
            console.error('数据监听检查失败:', error);
        }
    }
}

// 5. 检查页面刷新后的数据恢复
function checkDataRecovery() {
    console.log('\n--- 5. 检查数据恢复 ---');
    
    const currentUser = localStorage.getItem('currentUser');
    const topics = localStorage.getItem('topics');
    
    if (currentUser && topics) {
        try {
            const user = JSON.parse(currentUser);
            const allTopics = JSON.parse(topics);
            const userTopics = allTopics[user.username] || [];
            
            console.log('用户:', user.username);
            console.log('专题数量:', userTopics.length);
            console.log('数据恢复状态:', userTopics.length > 0 ? '✅ 成功' : '❌ 失败');
            
            if (userTopics.length > 0) {
                console.log('第一个专题:', userTopics[0]);
            }
        } catch (error) {
            console.error('数据恢复检查失败:', error);
        }
    } else {
        console.log('❌ 缺少必要的数据进行恢复检查');
    }
}

// 6. 生成调试报告
function generateDebugReport() {
    console.log('\n--- 6. 调试报告 ---');
    
    const report = {
        timestamp: new Date().toISOString(),
        localStorage: {
            hasData: localStorage.length > 0,
            keys: Object.keys(localStorage),
            size: JSON.stringify(localStorage).length
        },
        user: {
            isLoggedIn: !!localStorage.getItem('currentUser'),
            username: (() => {
                try {
                    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
                    return user.username || null;
                } catch {
                    return null;
                }
            })()
        },
        topics: {
            hasData: !!localStorage.getItem('topics'),
            userTopicsCount: (() => {
                try {
                    const user = JSON.parse(localStorage.getItem('currentUser') || '{}');
                    const allTopics = JSON.parse(localStorage.getItem('topics') || '{}');
                    return allTopics[user.username] ? allTopics[user.username].length : 0;
                } catch {
                    return 0;
                }
            })()
        },
        vueApp: {
            containerExists: !!document.querySelector('#app'),
            userInfoExists: !!document.querySelector('.nav-user span'),
            topicElementsCount: document.querySelectorAll('.topic-item').length
        }
    };
    
    console.log('调试报告:', report);
    
    // 保存报告到localStorage
    localStorage.setItem('debugReport', JSON.stringify(report));
    console.log('✅ 调试报告已保存到localStorage.debugReport');
    
    return report;
}

// 运行所有检查
function runAllChecks() {
    console.log('开始运行所有检查...\n');
    
    const results = {
        localStorage: checkLocalStorage(),
        vueApp: checkVueAppState(),
        dataOperations: simulateDataOperations(),
        dataWatchers: checkDataWatchers(),
        dataRecovery: checkDataRecovery()
    };
    
    console.log('\n--- 检查结果汇总 ---');
    Object.entries(results).forEach(([check, result]) => {
        console.log(`${check}: ${result ? '✅ 通过' : '❌ 失败'}`);
    });
    
    const report = generateDebugReport();
    
    console.log('\n=== 调试完成 ===');
    console.log('如果发现问题，请检查Console中的错误信息');
    console.log('调试报告已保存到localStorage.debugReport');
    
    return { results, report };
}

// 导出函数供手动调用
window.debugPersistence = {
    checkLocalStorage,
    checkVueAppState,
    simulateDataOperations,
    checkDataWatchers,
    checkDataRecovery,
    generateDebugReport,
    runAllChecks
};

console.log('调试函数已加载，可以调用:');
console.log('- debugPersistence.runAllChecks() 运行所有检查');
console.log('- debugPersistence.checkLocalStorage() 检查localStorage');
console.log('- debugPersistence.generateDebugReport() 生成调试报告'); 