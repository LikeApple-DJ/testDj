import React, { useState } from 'react';
import TabBar from './components/TabBar';
import HelloWorldTab from './components/HelloWorldTab';
import HashTab from './components/HashTab';
import SortTab from './components/SortTab';
import ExportButton from './components/ExportButton';

const TABS = [
  { key: 'helloworld', label: 'Hello World' },
  { key: 'hash', label: '哈希算法' },
  { key: 'sort', label: '冒泡排序' },
];

export default function App() {
  const [activeTab, setActiveTab] = useState('helloworld');

  const renderTabContent = () => {
    switch (activeTab) {
      case 'helloworld':
        return <HelloWorldTab />;
      case 'hash':
        return <HashTab />;
      case 'sort':
        return <SortTab />;
      default:
        return null;
    }
  };

  return (
    <div className="app-container">
      <h1 className="app-title">API 演示工具</h1>
      <div className="toolbar">
        <TabBar tabs={TABS} activeTab={activeTab} onTabChange={setActiveTab} />
        <ExportButton />
      </div>
      <div className="tab-content">
        {renderTabContent()}
      </div>
    </div>
  );
}