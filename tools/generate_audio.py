"""Create original short PCM sound effects. No samples or external licensing dependencies."""
from pathlib import Path
import math, struct, wave
root = Path(__file__).resolve().parents[1] / 'app/src/main/res/raw'
root.mkdir(parents=True, exist_ok=True)
for name, frequencies in [('chime', [523.25, 659.25, 783.99]), ('soft_note', [392, 440])]:
    samples=[]
    for freq in frequencies:
        length=.14
        for i in range(int(22050*length)):
            t=i/22050
            envelope=math.sin(math.pi*t/length)**2
            samples.append(struct.pack('<h', int(6000*envelope*math.sin(2*math.pi*freq*t))))
    with wave.open(str(root/(name+'.wav')), 'wb') as f:
        f.setnchannels(1);f.setsampwidth(2);f.setframerate(22050);f.writeframes(b''.join(samples))
print('Generated two original sound effects')
