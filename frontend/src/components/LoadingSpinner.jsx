import '../styles/App.css';

const LoadingSpinner = ({ text = 'Loading...' }) => {
  return (
    <div className="loading-spinner-container">
      <div className="spinner-ring">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
      <p className="spinner-text">{text}</p>
    </div>
  );
};

export default LoadingSpinner;
