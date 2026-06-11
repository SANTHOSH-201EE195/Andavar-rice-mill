import { useState, useEffect } from 'react';

export default function Typewriter({
    words,
    loop = true,
    typeSpeed = 100,
    deleteSpeed = 50,
    delaySpeed = 2000,
    className = ''
}) {
    const [text, setText] = useState('');
    const [isDeleting, setIsDeleting] = useState(false);
    const [loopNum, setLoopNum] = useState(0);
    const [typingSpeed, setTypingSpeed] = useState(typeSpeed);

    useEffect(() => {
        const handleType = () => {
            const i = loopNum % words.length;
            const fullText = words[i];

            setText(isDeleting
                ? fullText.substring(0, text.length - 1)
                : fullText.substring(0, text.length + 1)
            );

            setTypingSpeed(isDeleting ? deleteSpeed : typeSpeed);

            if (!isDeleting && text === fullText) {
                // Finished typing word
                if (!loop && loopNum === words.length - 1) return;

                setTimeout(() => setIsDeleting(true), delaySpeed);
            } else if (isDeleting && text === '') {
                // Finished deleting word
                setIsDeleting(false);
                setLoopNum(loopNum + 1);
            }
        };

        const timer = setTimeout(handleType, typingSpeed);
        return () => clearTimeout(timer);
    }, [text, isDeleting, loopNum, words, typeSpeed, deleteSpeed, delaySpeed]);

    return (
        <span className={className}>
            {text}
            <span className="cursor">|</span>
            <style>{`
                .cursor {
                    animation: blink 1s step-end infinite;
                    opacity: 1;
                    font-weight: 100;
                    margin-left: 2px;
                }
                @keyframes blink {
                    0%, 100% { opacity: 1; }
                    50% { opacity: 0; }
                }
            `}</style>
        </span>
    );
}
