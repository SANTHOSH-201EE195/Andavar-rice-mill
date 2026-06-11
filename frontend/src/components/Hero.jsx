import { useState } from 'react';
import { ArrowRight } from 'lucide-react';
import Typewriter from './Typewriter';
import './Hero.css';

const HoverText = ({ text, hoverText, className = '' }) => {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <span
            className={`hover-text-wrapper ${className}`}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
            style={{ cursor: 'pointer', transition: 'opacity 0.3s' }}
        >
            {isHovered ? hoverText : text}
        </span>
    );
};

export default function Hero() {
    return (
        <section className="hero">
            <div className="container hero-container">
                <div className="hero-content">
                    <span className="hero-subtitle">PURE, HEALTHY, NATURAL PRODUCTS</span>
                    <h1 className="hero-title">
                        <Typewriter
                            words={['Andavar', 'ஆண்டவர்']}
                            className="highlight-text"
                        /><br />
                        Oil & Masala
                    </h1>
                    <p className="hero-description">
                        <HoverText
                            text={<>We bring you authentic <strong>sekku pressed oils</strong> and <strong>traditionally prepared masalas</strong> made without preservatives or artificial additives. Pure ingredients and honest preparation for your family’s health.</>}
                            hoverText="நாங்கள் உங்களுக்கு கலப்படமற்ற செக்கு எண்ணெய்கள் மற்றும் பாரம்பரிய முறையில் தயாரிக்கப்பட்ட மசாலாக்களை வழங்குகிறோம். உங்கள் குடும்பத்தின் ஆரோக்கியத்திற்காக தூய்மையான பொருட்கள் மற்றும் நேர்மையான தயாரிப்பு."
                        />
                    </p>

                    <ul className="hero-features">
                        <li>
                            <div className="feature-icon"></div>
                            <HoverText
                                text="100% Sekku Pressed Oils"
                                hoverText="100% செக்கு எண்ணெய்கள்"
                            />
                        </li>
                        <li>
                            <div className="feature-icon"></div>
                            <HoverText
                                text="Freshly Ground Traditional Masalas"
                                hoverText="புதிதாக அரைக்கப்பட்ட பாரம்பரிய மசாலாக்கள்"
                            />
                        </li>
                        <li>
                            <div className="feature-icon"></div>
                            <HoverText
                                text="No Preservatives. No Chemicals."
                                hoverText="எந்த இரசாயனமும் கலக்கப்படவில்லை."
                            />
                        </li>
                    </ul>

                    <div className="hero-cta">
                        <button
                            className="btn btn-primary btn-lg"
                            onClick={() => document.getElementById('shop')?.scrollIntoView({ behavior: 'smooth' })}
                        >
                            Shop Now <ArrowRight size={20} style={{ marginLeft: '8px' }} />
                        </button>
                        <div className="hero-contact">
                            <div className="contact-icon">📞</div>
                            <div className="contact-text">
                                <span className="label">Call Us Anytime</span>
                                <span className="number">+91 7010903976</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="hero-image">
                    <div className="image-wrapper">
                        <img src="/images/sekku.png" alt="Traditional Sekku Oil Extraction" className="hero-main-img" />
                        <div className="trust-badge">
                            <div className="badge-circle">
                                <span className="badge-text">100% NATURAL</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
